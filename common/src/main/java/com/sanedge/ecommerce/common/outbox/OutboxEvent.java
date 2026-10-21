package com.sanedge.ecommerce.common.outbox;

import java.time.Instant;

import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Base class for per-module outbox events.
 *
 * <p>An outbox row is written in the same local (MongoDB) transaction as the
 * business write, then a relay ({@link KafkaOutboxPublisher}) drains pending
 * rows and publishes them to Kafka. This gives at-least-once delivery without a
 * distributed transaction: if the relay crashes mid-send, the event is
 * re-claimed after its lease and retried.
 *
 * <p>Subclasses are concrete {@code @Document} entities; the shared fields below
 * are persisted as part of each subclass document.
 */
@Getter
@Setter
@NoArgsConstructor
public abstract class OutboxEvent {

    @Id
    private String id;

    private String topic;

    private String eventKey;

    private String payload;

    /** {@code PENDING} until published, then {@code SENT} (or {@code FAILED} as dead letter). */
    private String status = OutboxStatus.PENDING.name();

    private String claimToken;

    private int attempts = 0;

    private Instant createdAt = Instant.now();

    private Instant claimedAt;

    private Instant nextAttemptAt;

    private Instant sentAt;

    private String lastError;
}
