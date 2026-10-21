package com.sanedge.ecommerce.shipping_address.service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.shipping_address.domain.requests.CreateShippingAddressRequest;
import com.sanedge.ecommerce.shipping_address.domain.requests.UpdateShippingAddressRequest;
import com.sanedge.ecommerce.shipping_address.domain.responses.ShippingAddressResponse;
import com.sanedge.ecommerce.shipping_address.domain.responses.ShippingAddressResponseDeleteAt;

public interface ShippingAddressCommandService {
    ApiResponse<ShippingAddressResponse> create(CreateShippingAddressRequest request);

    ApiResponse<ShippingAddressResponse> update(UpdateShippingAddressRequest request);

    ApiResponse<ShippingAddressResponseDeleteAt> trash(Integer shippingId);

    ApiResponse<ShippingAddressResponseDeleteAt> restore(Integer shippingId);

    ApiResponse<Boolean> deletePermanently(Integer shippingId);

    ApiResponse<Boolean> restoreAll();

    ApiResponse<Boolean> deleteAllPermanent();
}
