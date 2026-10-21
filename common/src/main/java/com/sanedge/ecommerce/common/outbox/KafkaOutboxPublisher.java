package com.sanedge.ecommerce.common.outbox;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;

import com.sanedge.ecommerce.common.chaos.ChaosManager;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * Relay that drains a module's outbox and publishes each event to Kafka.
 *
 * <p>Runs on a fixed schedule. For every due event it claims the row (lease), sends the payload
 * to the configured topic, then marks the row sent. A send failure (or a chaos policy) releases
 * the row for a later retry, so delivery is at-least-once; once the retry budget is exhausted the
 * row is parked as a dead letter ({@code FAILED}). The whole point of the outbox is that this runs
 * independently of the business write path.
 *
 * @param <T> the concrete outbox event type
 */
public abstract class KafkaOutboxPublisher<T extends OutboxEvent> {

    private static final Logger log = LoggerFactory.getLogger(KafkaOutboxPublisher.class);
    private static final long SEND_TIMEOUT_SECONDS = 30;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ChaosManager chaosManager;

    protected KafkaOutboxPublisher(KafkaTemplate<String, String> kafkaTemplate, ChaosManager chaosManager) {
        this(kafkaTemplate, chaosManager, null);
    }

    protected KafkaOutboxPublisher(KafkaTemplate<String, String> kafkaTemplate, ChaosManager chaosManager,
            ObjectProvider<MeterRegistry> meterRegistry) {
        this.kafkaTemplate = kafkaTemplate;
        this.chaosManager = chaosManager;
        registerMetrics(meterRegistry == null ? null : meterRegistry.getIfAvailable());
    }

    /** Logical module name, used for logging and chaos targeting. */
    protected abstract String moduleName();

    /** The repository this relay drains. */
    protected abstract OutboxEventRepository<T> repository();

    /** Max events published per scheduler tick. */
    protected abstract int batchSize();

    /** Seconds to wait before a failed event is retried. */
    protected long retryDelaySeconds() {
        return 30;
    }

    /** How often a failed event may be retried before it becomes a dead letter. */
    protected int maxAttempts() {
        return 5;
    }

    @Scheduled(fixedDelayString = "${outbox.publish.interval-ms:1000}")
    public void publish() {
        List<T> due = repository().findDue(batchSize());
        if (due.isEmpty()) {
            return;
        }
        for (T event : due) {
            T claimed = repository().claim(event);
            if (claimed == null) {
                // another relay instance owns this row
                continue;
            }
            try {
                maybeInjectChaos();
                CompletableFuture<SendResult<String, String>> future =
                        kafkaTemplate.send(new ProducerRecord<>(claimed.getTopic(), claimed.getEventKey(), claimed.getPayload()));
                future.get(SEND_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                repository().markSent(claimed);
                countPublished();
            } catch (Exception e) {
                log.warn("[{}] outbox publish failed for event {}: {}", moduleName(), claimed.getId(), e.getMessage());
                if (claimed.getAttempts() >= maxAttempts()) {
                    repository().markFailed(claimed, e, retryDelaySeconds());
                    countDeadLetter();
                    log.error("[{}] outbox event {} parked as dead letter after {} attempts",
                            moduleName(), claimed.getId(), claimed.getAttempts());
                } else {
                    repository().markRetry(claimed, e, retryDelaySeconds());
                }
            }
        }
    }

    private void maybeInjectChaos() {
        if (chaosManager != null && chaosManager.evaluate("kafka", moduleName()) != null) {
            throw new RuntimeException("chaos: simulated Kafka failure for module " + moduleName());
        }
    }

    private Counter publishedCounter;
    private Counter deadLetterCounter;

    private void registerMetrics(MeterRegistry registry) {
        if (registry == null) {
            return;
        }
        Gauge.builder("outbox.events.pending", this, publisher -> publisher.repository().countPending())
                .description("Outbox rows waiting for publication")
                .tag("module", moduleName())
                .register(registry);

        publishedCounter = Counter.builder("outbox.events.published")
                .description("Outbox rows successfully published to Kafka")
                .tag("module", moduleName())
                .register(registry);
        deadLetterCounter = Counter.builder("outbox.events.failed")
                .description("Outbox rows parked as dead letters")
                .tag("module", moduleName())
                .register(registry);
    }

    private void countPublished() {
        if (publishedCounter != null) {
            publishedCounter.increment();
        }
    }

    private void countDeadLetter() {
        if (deadLetterCounter != null) {
            deadLetterCounter.increment();
        }
    }
}
