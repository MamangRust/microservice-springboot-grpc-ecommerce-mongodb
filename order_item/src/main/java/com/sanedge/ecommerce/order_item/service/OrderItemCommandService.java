package com.sanedge.ecommerce.order_item.service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.order_item.domain.requests.CreateOrderItemRequest;
import com.sanedge.ecommerce.order_item.domain.requests.UpdateOrderItemRequest;
import com.sanedge.ecommerce.order_item.domain.responses.OrderItemResponse;
import com.sanedge.ecommerce.order_item.domain.responses.OrderItemResponseDeleteAt;

public interface OrderItemCommandService {
    ApiResponse<OrderItemResponse> create(CreateOrderItemRequest request);

    ApiResponse<OrderItemResponse> update(UpdateOrderItemRequest request);

    ApiResponse<OrderItemResponseDeleteAt> trash(Long id);

    ApiResponse<OrderItemResponseDeleteAt> restore(Long id);

    ApiResponse<Boolean> deletePermanent(Long id);

    ApiResponse<Boolean> restoreAll();

    ApiResponse<Boolean> deleteAll();

    ApiResponse<Boolean> deleteByOrderPermanent(Long orderId);

    ApiResponse<Boolean> deleteByOrderRollback(Long orderId);

    ApiResponse<Integer> calculateTotalPrice(Long orderId);
}
