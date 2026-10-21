package com.sanedge.ecommerce.gateway.dto.responses.orderitem;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDeleteAt {
    private Long id;
    private Integer orderId;
    private Integer productId;
    private Integer quantity;
    private Integer price;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;

}