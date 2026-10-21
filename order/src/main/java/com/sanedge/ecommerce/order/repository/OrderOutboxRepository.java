package com.sanedge.ecommerce.order.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.common.outbox.MongoOutboxEventRepository;
import com.sanedge.ecommerce.order.models.OrderOutbox;

@Repository
public class OrderOutboxRepository extends MongoOutboxEventRepository<OrderOutbox> {

    public OrderOutboxRepository(MongoTemplate mongoTemplate,
            @Value("${order.outbox.lease.seconds:300}") long leaseSeconds) {
        super(mongoTemplate, OrderOutbox.class, leaseSeconds);
    }
}
