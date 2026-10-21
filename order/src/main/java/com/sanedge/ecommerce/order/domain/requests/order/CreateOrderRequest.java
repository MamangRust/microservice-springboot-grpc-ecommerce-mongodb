package com.sanedge.ecommerce.order.domain.requests.order;

import java.util.List;

import com.sanedge.ecommerce.order.domain.requests.shipping.CreateShippingAddressRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderRequest {
    @NotNull
    private Integer merchantId;

    @NotNull
    private Integer userId;

    @Valid
    @NotNull
    private List<CreateOrderItemRequest> items;

    @Valid
    @NotNull
    private CreateShippingAddressRequest shippingAddress;
}
