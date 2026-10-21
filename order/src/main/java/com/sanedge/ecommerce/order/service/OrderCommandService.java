package com.sanedge.ecommerce.order.service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.order.domain.requests.order.CreateOrderRequest;
import com.sanedge.ecommerce.order.domain.requests.order.UpdateOrderRequest;
import com.sanedge.ecommerce.order.domain.responses.order.OrderResponse;
import com.sanedge.ecommerce.order.domain.responses.order.OrderResponseDeleteAt;

public interface OrderCommandService {
    ApiResponse<OrderResponse> create(CreateOrderRequest request);

    ApiResponse<OrderResponse> update(UpdateOrderRequest request);

    ApiResponse<OrderResponseDeleteAt> trash(Integer id);

    ApiResponse<OrderResponseDeleteAt> restore(Integer id);

    ApiResponse<Boolean> delete(Integer id);

    ApiResponse<Boolean> restoreAll();

    ApiResponse<Boolean> deleteAll();
}
