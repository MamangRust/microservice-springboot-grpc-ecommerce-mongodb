package com.sanedge.ecommerce.shipping_address.service;

import java.util.List;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.shipping_address.domain.requests.FindAllShippingAddress;
import com.sanedge.ecommerce.shipping_address.domain.responses.ShippingAddressResponse;
import com.sanedge.ecommerce.shipping_address.domain.responses.ShippingAddressResponseDeleteAt;

public interface ShippingAddressQueryService {

    ApiResponsePagination<List<ShippingAddressResponse>> findAll(FindAllShippingAddress req);

    ApiResponsePagination<List<ShippingAddressResponseDeleteAt>> findByActive(FindAllShippingAddress req);

    ApiResponsePagination<List<ShippingAddressResponseDeleteAt>> findByTrashed(FindAllShippingAddress req);

    ApiResponse<ShippingAddressResponse> findById(Integer shippingId);

    ApiResponse<ShippingAddressResponse> findByOrder(Integer orderId);
}
