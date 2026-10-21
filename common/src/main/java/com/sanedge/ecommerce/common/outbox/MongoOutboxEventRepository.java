package com.sanedge.ecommerce.common.outbox;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * MongoDB-backed implementation of {@link OutboxEventRepository}.
 *
 * <p>Uses a lease-based claim so multiple relay instances (or a crashed relay
 * that restarts) cannot publish the same event twice: a claimed row is invisible
 * to other claimers until its lease expires, after which it becomes claimable
 * again. This yields at-least-once delivery.
 *
 * <p>The claim is a single atomic {@code findAndModify}: the row is stamped with a
 * {@code claimToken} and {@code claimedAt}, and only the relay holding that token may mark the
 * row sent, retry it or park it as a dead letter.
 *
 * @param <T> the concrete outbox event type
 */
public abstract class MongoOutboxEventRepository<T extends OutboxEvent> implements OutboxEventRepository<T> {

    protected final MongoTemplate mongoTemplate;
    private final Class<T> entityClass;
    private final long leaseSeconds;

    protected MongoOutboxEventRepository(MongoTemplate mongoTemplate, Class<T> entityClass, long leaseSeconds) {
        this.mongoTemplate = mongoTemplate;
        this.entityClass = entityClass;
        this.leaseSeconds = Math.max(1, leaseSeconds);
    }

    @Override
    public void enqueue(T event) {
        if (event.getStatus() == null) {
            event.setStatus(OutboxStatus.PENDING.name());
        }
        mongoTemplate.save(event);
    }

    @Override
    public List<T> findDue(int limit) {
        Query query = new Query(dueCriteria(Instant.now()))
                .with(Sort.by(Sort.Direction.ASC, "createdAt"))
                .limit(Math.max(1, limit));
        return mongoTemplate.find(query, entityClass);
    }

    @Override
    public T claim(T event) {
        if (event == null || event.getId() == null) {
            return null;
        }
        Instant now = Instant.now();
        String token = UUID.randomUUID().toString();

        Query query = new Query(dueCriteria(now).and("_id").is(event.getId()));
        Update update = new Update()
                .set("claimedAt", now)
                .set("claimToken", token)
                .inc("attempts", 1);

        return mongoTemplate.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true), entityClass);
    }

    @Override
    public void markSent(T event) {
        Query query = ownedQuery(event);
        Update update = new Update()
                .set("status", OutboxStatus.SENT.name())
                .set("sentAt", Instant.now())
                .set("claimedAt", null)
                .set("nextAttemptAt", null)
                .set("lastError", null);
        mongoTemplate.updateFirst(query, update, entityClass);
    }

    @Override
    public void markRetry(T event, Throwable failure, long delaySeconds) {
        Instant next = Instant.now().plusSeconds(Math.max(0, delaySeconds));
        Query query = ownedQuery(event);
        Update update = new Update()
                .set("status", OutboxStatus.PENDING.name())
                .set("claimedAt", null)
                .set("lastError", failure == null ? "unknown failure" : failure.getMessage())
                .set("nextAttemptAt", next);
        mongoTemplate.updateFirst(query, update, entityClass);
    }

    /** Parks a claimed row as a dead letter — it is never retried again. */
    @Override
    public void markFailed(T event, Throwable failure, long delaySeconds) {
        Query query = ownedQuery(event);
        Update update = new Update()
                .set("status", OutboxStatus.FAILED.name())
                .set("sentAt", Instant.now())
                .set("claimedAt", null)
                .set("lastError", failure == null ? "unknown failure" : failure.getMessage());
        mongoTemplate.updateFirst(query, update, entityClass);
    }

    @Override
    public long countPending() {
        return mongoTemplate.count(new Query(Criteria.where("status").is(OutboxStatus.PENDING.name())), entityClass);
    }

    private Criteria dueCriteria(Instant now) {
        Instant leaseCutoff = now.minusSeconds(leaseSeconds);
        return Criteria.where("status").is(OutboxStatus.PENDING.name())
                .andOperator(
                        new Criteria().orOperator(
                                Criteria.where("nextAttemptAt").is(null),
                                Criteria.where("nextAttemptAt").lte(now)),
                        new Criteria().orOperator(
                                Criteria.where("claimedAt").is(null),
                                Criteria.where("claimedAt").lte(leaseCutoff)));
    }

    /** A row may only be updated by the relay that currently owns its claim token. */
    private Query ownedQuery(T event) {
        Criteria criteria = Criteria.where("_id").is(event.getId())
                .and("status").is(OutboxStatus.PENDING.name());
        if (event.getClaimToken() != null) {
            criteria = criteria.and("claimToken").is(event.getClaimToken());
        }
        return new Query(criteria);
    }
}
