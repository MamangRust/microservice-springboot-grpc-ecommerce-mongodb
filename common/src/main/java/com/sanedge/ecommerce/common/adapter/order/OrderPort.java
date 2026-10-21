package com.sanedge.ecommerce.common.adapter.order;

import com.sanedge.ecommerce.common.adapter.model.Order;
import com.sanedge.ecommerce.common.adapter.support.Paged;

/**
 * Port order. Setara {@code order.QueryRepository} + {@code order.BulkRepository}
 * di Go. Error status gRPC dipropagasikan apa adanya (NotFound -&gt; 404).
 */
public interface OrderPort {

    Order findById(int orderId);

    Paged<Order> findAll(int page, int pageSize);
}
