package com.sanedge.ecommerce.order.repository;

import com.sanedge.ecommerce.order.models.Order;

public interface OrderCommandRepositoryCustom {
    Order trashed(String orderId);

    Order restore(String orderId);

    Order deletePermanent(String orderId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
