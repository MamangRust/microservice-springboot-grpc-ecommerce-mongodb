package com.sanedge.ecommerce.order_item.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.order_item.models.OrderItem;

@Repository
public interface OrderItemCommandRepository extends MongoRepository<OrderItem, String>,
        OrderItemCommandRepositoryCustom {
}
