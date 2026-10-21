package com.sanedge.ecommerce.order.service;

import java.util.List;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.order.domain.requests.order.FindAllOrderByMerchantRequest;
import com.sanedge.ecommerce.order.domain.requests.order.FindAllOrderRequest;
import com.sanedge.ecommerce.order.domain.responses.order.OrderResponse;
import com.sanedge.ecommerce.order.domain.responses.order.OrderResponseDeleteAt;

public interface OrderQueryService {
    ApiResponsePagination<List<OrderResponse>> findAll(FindAllOrderRequest req);

    ApiResponsePagination<List<OrderResponseDeleteAt>> findByActive(FindAllOrderRequest req);

    ApiResponsePagination<List<OrderResponseDeleteAt>> findByTrashed(FindAllOrderRequest req);

    ApiResponse<OrderResponse> findById(Integer id);

    ApiResponsePagination<List<OrderResponse>> findByMerchantId(FindAllOrderByMerchantRequest req);
}
