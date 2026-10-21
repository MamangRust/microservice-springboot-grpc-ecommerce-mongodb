package com.sanedge.ecommerce.transaction.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.common.outbox.MongoOutboxEventRepository;
import com.sanedge.ecommerce.transaction.models.TransactionOutbox;

@Repository
public class TransactionOutboxRepository extends MongoOutboxEventRepository<TransactionOutbox> {

    public TransactionOutboxRepository(MongoTemplate mongoTemplate,
            @Value("${transaction.outbox.lease.seconds:300}") long leaseSeconds) {
        super(mongoTemplate, TransactionOutbox.class, leaseSeconds);
    }
}
