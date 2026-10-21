package com.sanedge.ecommerce.common.outbox;

import java.util.List;

/**
 * Persistence contract for an outbox table, implemented per module on top of
 * MongoDB by {@link MongoOutboxEventRepository}.
 *
 * @param <T> the concrete outbox event type
 */
public interface OutboxEventRepository<T extends OutboxEvent> {

    /** Persist a new pending event. */
    void enqueue(T event);

    /** Return up to {@code limit} publishable events (PENDING and not leased). */
    List<T> findDue(int limit);

    /**
     * Atomically claim a single event for publishing. Returns the claimed event,
     * or {@code null} if it was claimed by another relay instance first.
     */
    T claim(T event);

    /** Mark a claimed event as successfully published. */
    void markSent(T event);

    /** Release a claimed event back to PENDING for a later retry. */
    void markRetry(T event, Throwable failure, long delaySeconds);

    /** Park a claimed event as a dead letter — it is never retried again. */
    void markFailed(T event, Throwable failure, long delaySeconds);

    /** Count of events still awaiting publication. */
    long countPending();
}
