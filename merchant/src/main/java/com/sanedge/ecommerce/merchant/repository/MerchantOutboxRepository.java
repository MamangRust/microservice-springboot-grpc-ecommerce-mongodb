package com.sanedge.ecommerce.merchant.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.common.outbox.MongoOutboxEventRepository;
import com.sanedge.ecommerce.merchant.models.MerchantOutbox;

@Repository
public class MerchantOutboxRepository extends MongoOutboxEventRepository<MerchantOutbox> {

    public MerchantOutboxRepository(MongoTemplate mongoTemplate,
            @Value("${merchant.outbox.lease.seconds:300}") long leaseSeconds) {
        super(mongoTemplate, MerchantOutbox.class, leaseSeconds);
    }
}
