package com.sanedge.ecommerce.order_item.service;

import java.util.List;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.order_item.domain.requests.FindAllOrderItems;
import com.sanedge.ecommerce.order_item.domain.responses.OrderItemResponse;
import com.sanedge.ecommerce.order_item.domain.responses.OrderItemResponseDeleteAt;

public interface OrderItemQueryService {
    ApiResponsePagination<List<OrderItemResponse>> findAll(FindAllOrderItems req);

    ApiResponsePagination<List<OrderItemResponseDeleteAt>> findByActive(FindAllOrderItems req);

    ApiResponsePagination<List<OrderItemResponseDeleteAt>> findByTrashed(FindAllOrderItems req);

    ApiResponse<List<OrderItemResponse>> findByOrder(Long orderId);
}
