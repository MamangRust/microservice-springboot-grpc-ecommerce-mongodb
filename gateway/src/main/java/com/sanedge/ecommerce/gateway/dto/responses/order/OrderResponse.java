package com.sanedge.ecommerce.gateway.dto.responses.order;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long id;
    private Integer merchantId;
    private Integer userId;
    private Integer totalPrice;
    private String createdAt;
    private String updatedAt;

}
