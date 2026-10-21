package com.sanedge.ecommerce.cart.service;

import java.util.List;

import com.sanedge.ecommerce.cart.domain.requests.CreateCartRequest;
import com.sanedge.ecommerce.cart.domain.requests.DeleteCartRequest;
import com.sanedge.ecommerce.cart.domain.requests.FindAllCartsRequest;
import com.sanedge.ecommerce.cart.domain.responses.CartResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;

public interface CartService {
    ApiResponsePagination<List<CartResponse>> findAll(FindAllCartsRequest req);

    ApiResponse<CartResponse> createCart(CreateCartRequest req);

    ApiResponse<Boolean> deletePermanent(Long cartId);

    ApiResponse<Boolean> deleteAllPermanently(DeleteCartRequest req);

}
