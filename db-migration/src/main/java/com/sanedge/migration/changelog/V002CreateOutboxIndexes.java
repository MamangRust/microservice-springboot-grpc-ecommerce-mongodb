package com.sanedge.migration.changelog;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.core.index.IndexOperations;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;

/**
 * Creates the indexes for the transactional outbox collections.
 *
 * <p>Each module owns its own outbox collection ({@code order_outbox},
 * {@code transaction_outbox}, {@code merchant_outbox}) inside its bounded-context database.
 *
 * <p>The relay polls {@code findDue(limit)}: rows that are {@code status=PENDING}, past their
 * {@code nextAttemptAt} backoff and either unclaimed or with an expired {@code claimedAt} lease,
 * ordered by {@code createdAt}. The compound index below matches that access pattern; without it
 * every tick would scan the whole collection.
 */
@ChangeUnit(id = "002-create-outbox-indexes", order = "002", author = "ecommerce")
public class V002CreateOutboxIndexes {

    private static final List<String> OUTBOX_COLLECTIONS =
            List.of("order_outbox", "transaction_outbox", "merchant_outbox");
    private static final String IX_OUTBOX_DUE = "ix_outbox_due";
    private static final String IX_OUTBOX_CREATED = "ix_outbox_created";

    @Execution
    public void migrate(MongoTemplate template) {
        for (String collection : OUTBOX_COLLECTIONS) {
            IndexOperations outbox = template.indexOps(collection);
            outbox.ensureIndex(new Index()
                    .on("status", Sort.Direction.ASC)
                    .on("nextAttemptAt", Sort.Direction.ASC)
                    .on("claimedAt", Sort.Direction.ASC)
                    .named(IX_OUTBOX_DUE));
            outbox.ensureIndex(new Index()
                    .on("createdAt", Sort.Direction.ASC)
                    .named(IX_OUTBOX_CREATED));
        }
    }

    /**
     * Drops the indexes created by {@link #migrate(MongoTemplate)}. Documents are left untouched.
     */
    @RollbackExecution
    public void rollback(MongoTemplate template) {
        for (String collection : OUTBOX_COLLECTIONS) {
            dropIndexes(template, collection, List.of(IX_OUTBOX_DUE, IX_OUTBOX_CREATED));
        }
    }

    private void dropIndexes(MongoTemplate template, String collection, List<String> names) {
        IndexOperations ops = template.indexOps(collection);
        for (IndexInfo index : ops.getIndexInfo()) {
            if (names.contains(index.getName())) {
                ops.dropIndex(index.getName());
            }
        }
    }
}
