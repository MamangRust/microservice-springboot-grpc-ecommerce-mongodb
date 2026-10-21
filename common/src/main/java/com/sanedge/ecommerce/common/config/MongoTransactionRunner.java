package com.sanedge.ecommerce.common.config;

import java.util.function.Supplier;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Runs a block of MongoDB work inside a transaction when one is available.
 *
 * <p>Used by the command services to make the business write and the outbox insert atomic: both
 * land in the same MongoDB transaction, so an event exists if and only if the business row
 * exists. When no transaction manager is configured (standalone MongoDB, or
 * {@code outbox.transaction.enabled=false}) the callback simply runs without a transaction.
 */
@Component
public class MongoTransactionRunner {

    private final ObjectProvider<TransactionTemplate> transactionTemplate;

    public MongoTransactionRunner(ObjectProvider<TransactionTemplate> transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    /** Executes {@code action} in a transaction and returns its result. */
    public <T> T call(Supplier<T> action) {
        TransactionTemplate template = transactionTemplate.getIfAvailable();
        if (template == null) {
            return action.get();
        }
        return template.execute(status -> action.get());
    }

    /** Executes {@code action} in a transaction. */
    public void run(Runnable action) {
        call(() -> {
            action.run();
            return null;
        });
    }
}
