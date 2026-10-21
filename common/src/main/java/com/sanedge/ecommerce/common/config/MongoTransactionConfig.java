package com.sanedge.ecommerce.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * MongoDB transactions, used to make the business write and the outbox insert atomic.
 *
 * <p>Multi-document transactions require a replica set (the local/k8s MongoDB runs as a
 * single-node {@code rs0}); the connection URIs therefore carry {@code ?replicaSet=rs0}.
 *
 * <p>The transaction manager is only registered when {@code outbox.transaction.enabled} is
 * {@code true} (the default). Modules that must keep working against a standalone MongoDB can
 * switch it off — {@link MongoTransactionRunner} then simply executes the callback without a
 * transaction.
 */
@Configuration
@ConditionalOnClass(name = {
        "org.springframework.data.mongodb.MongoTransactionManager",
        "com.mongodb.client.MongoClient"
})
@ConditionalOnProperty(name = "outbox.transaction.enabled", havingValue = "true", matchIfMissing = true)
public class MongoTransactionConfig {

    private static final Logger log = LoggerFactory.getLogger(MongoTransactionConfig.class);

    @Bean
    @ConditionalOnMissingBean
    public MongoTransactionManager mongoTransactionManager(MongoDatabaseFactory databaseFactory) {
        return new MongoTransactionManager(databaseFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager,
            @Value("${spring.data.mongodb.uri:}") String uri) {
        if (uri != null && !uri.isBlank() && !uri.contains("replicaSet")) {
            // Not fatal (the driver may auto-discover the set), but a missing parameter is the
            // usual cause of "Transaction numbers are only allowed on a replica set member".
            log.warn("⚠️ MongoDB URI has no replicaSet parameter — transactions require a replica set");
        }
        return new TransactionTemplate(transactionManager);
    }
}
