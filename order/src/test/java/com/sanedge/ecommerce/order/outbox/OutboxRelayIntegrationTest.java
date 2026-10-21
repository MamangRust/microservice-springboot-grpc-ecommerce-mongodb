package com.sanedge.ecommerce.order.outbox;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.common.outbox.OutboxStatus;
import com.sanedge.ecommerce.order.models.OrderOutbox;
import com.sanedge.ecommerce.order.repository.OrderOutboxRepository;

/**
 * Relay outbox: claim hanya boleh diambil satu relay (lease + token), retry punya backoff, dan
 * row yang berhasil dipublish ditandai {@code SENT}.
 *
 * <p>Semua test memakai MongoDB replica set sungguhan (testcontainers) supaya perilaku atomic
 * {@code findAndModify} benar-benar teruji.
 */
class OutboxRelayIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private OrderOutboxRepository repository;

    /** Relay kedua — meniru replica lain dari service yang sama. */
    private OrderOutboxRepository otherReplica() {
        return new OrderOutboxRepository(mongoTemplate, 300);
    }

    private OrderOutbox event() {
        OrderOutbox outbox = new OrderOutbox();
        outbox.setId("event-" + System.nanoTime());
        outbox.setTopic("stats.ecommerce.order.event");
        outbox.setEventKey("42");
        outbox.setPayload("{}");
        return outbox;
    }

    @Test
    void claimIsExclusiveBetweenReplicas() {
        repository.enqueue(event());

        OrderOutbox candidate = repository.findDue(10).get(0);

        OrderOutbox claimedByFirst = repository.claim(candidate);
        OrderOutbox claimedBySecond = otherReplica().claim(candidate);

        assertThat(claimedByFirst).isNotNull();
        assertThat(claimedByFirst.getClaimToken()).isNotBlank();
        assertThat(claimedBySecond).isNull();
    }

    @Test
    void claimedRowIsInvisibleToOtherRelays() {
        repository.enqueue(event());
        OrderOutbox candidate = repository.findDue(10).get(0);

        repository.claim(candidate);

        assertThat(otherReplica().findDue(10)).isEmpty();
    }

    @Test
    void expiredLeaseMakesTheRowClaimableAgain() throws InterruptedException {
        // Kedua relay memakai durasi lease yang sama (konfigurasi bersama per service).
        OrderOutboxRepository first = new OrderOutboxRepository(mongoTemplate, 1);
        OrderOutboxRepository second = new OrderOutboxRepository(mongoTemplate, 1);
        first.enqueue(event());

        OrderOutbox candidate = first.findDue(10).get(0);
        assertThat(first.claim(candidate)).isNotNull();

        Thread.sleep(1_200);

        OrderOutbox reclaimed = second.claim(candidate);
        assertThat(reclaimed).isNotNull();
        assertThat(reclaimed.getClaimToken()).isNotEqualTo(candidate.getClaimToken());
    }

    @Test
    void retryReleasesTheRowWithBackoff() {
        repository.enqueue(event());
        OrderOutbox candidate = repository.findDue(10).get(0);
        OrderOutbox claimed = repository.claim(candidate);

        repository.markRetry(claimed, new IllegalStateException("kafka down"), 60);

        assertThat(repository.findDue(10)).isEmpty();
        OrderOutbox stored = mongoTemplate.findById(claimed.getId(), OrderOutbox.class);
        assertThat(stored.getStatus()).isEqualTo(OutboxStatus.PENDING.name());
        assertThat(stored.getLastError()).contains("kafka down");
        assertThat(stored.getNextAttemptAt()).isAfter(Instant.now());
        assertThat(stored.getClaimedAt()).isNull();
    }

    @Test
    void sentRowIsMarkedSent() {
        repository.enqueue(event());
        OrderOutbox candidate = repository.findDue(10).get(0);
        OrderOutbox claimed = repository.claim(candidate);

        repository.markSent(claimed);

        OrderOutbox stored = mongoTemplate.findById(claimed.getId(), OrderOutbox.class);
        assertThat(stored.getStatus()).isEqualTo(OutboxStatus.SENT.name());
        assertThat(stored.getSentAt()).isNotNull();
        assertThat(repository.countPending()).isZero();
    }

    @Test
    void deadLetteredRowIsNeverPickedUpAgain() {
        repository.enqueue(event());
        OrderOutbox candidate = repository.findDue(10).get(0);
        OrderOutbox claimed = repository.claim(candidate);

        repository.markFailed(claimed, new IllegalStateException("broker down"), 0);

        assertThat(repository.findDue(10)).isEmpty();
        OrderOutbox stored = mongoTemplate.findById(claimed.getId(), OrderOutbox.class);
        assertThat(stored.getStatus()).isEqualTo(OutboxStatus.FAILED.name());
    }
}
