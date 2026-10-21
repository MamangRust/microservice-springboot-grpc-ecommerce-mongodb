package com.sanedge.ecommerce.order_item.repository;

import com.sanedge.ecommerce.order_item.models.OrderItem;

public interface OrderItemCommandRepositoryCustom {
    OrderItem trashed(String orderItemId);

    OrderItem restore(String orderItemId);

    OrderItem deletePermanent(String orderItemId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();

    boolean deleteByOrderPermanent(Long orderId);

    boolean deleteByOrderRollback(Long orderId);
}
