package com.sanedge.ecommerce.order_item.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.order_item.models.OrderItem;

@Repository
public interface OrderItemQueryRepository extends MongoRepository<OrderItem, String> {

    @Query("{}")
    Page<OrderItem> findOrderItems(Pageable pageable);

    @Query("{ 'deleted_at': null }")
    Page<OrderItem> findActiveOrderItems(Pageable pageable);

    @Query("{ 'deleted_at': { '$ne': null } }")
    Page<OrderItem> findTrashedOrderItems(Pageable pageable);

    @Query("{ 'order_id': ?0, 'deleted_at': null }")
    List<OrderItem> findOrderItemByOrder(@Param("orderId") Long orderId);
}
