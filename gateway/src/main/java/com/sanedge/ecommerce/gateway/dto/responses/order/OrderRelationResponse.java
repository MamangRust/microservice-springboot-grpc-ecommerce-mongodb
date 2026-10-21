package com.sanedge.ecommerce.gateway.dto.responses.order;

import java.util.List;

import com.sanedge.ecommerce.gateway.dto.responses.orderitem.OrderItemResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRelationResponse {
    private Long orderId;

    private Integer userId;

    private Integer merchantId;

    private Integer totalPrice;

    private List<OrderItemResponse> orderItems;

}
