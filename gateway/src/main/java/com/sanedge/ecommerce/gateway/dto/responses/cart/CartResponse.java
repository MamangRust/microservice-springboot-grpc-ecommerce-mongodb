package com.sanedge.ecommerce.gateway.dto.responses.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {

    private Long id;
    private Integer userId;
    private Integer productId;
    private String name;
    private Integer price;
    private String image;
    private Integer quantity;
    private Integer weight;
    private String createdAt;
    private String updatedAt;

}