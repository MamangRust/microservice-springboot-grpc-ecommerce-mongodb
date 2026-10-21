package com.sanedge.ecommerce.cart.domain.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.sanedge.ecommerce.cart.models.Cart;
import com.sanedge.ecommerce.common.utils.Ids;

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

    public static CartResponse from(Cart cart) {
        return CartResponse.builder()
                .id(Ids.toLong(cart.getCartId()))
                .userId(cart.getUserId())
                .productId(cart.getProductId())
                .name(cart.getName())
                .price(cart.getPrice())
                .image(cart.getImage())
                .quantity(cart.getQuantity())
                .weight(cart.getWeight())
                .createdAt(cart.getCreatedAt() != null ? cart.getCreatedAt().toString() : null)
                .updatedAt(cart.getUpdatedAt() != null ? cart.getUpdatedAt().toString() : null)
                .build();
    }
}
