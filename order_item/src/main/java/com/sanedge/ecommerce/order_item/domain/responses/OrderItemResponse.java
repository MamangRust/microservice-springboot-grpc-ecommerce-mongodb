package com.sanedge.ecommerce.order_item.domain.responses;

import com.sanedge.ecommerce.common.utils.Ids;
import com.sanedge.ecommerce.order_item.models.OrderItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {

    private Long id;
    private Integer orderId;
    private Integer productId;
    private Integer quantity;
    private Integer price;
    private String createdAt;
    private String updatedAt;

    public static OrderItemResponse from(OrderItem entity) {
        if (entity == null) {
            return null;
        }
        return OrderItemResponse.builder()
                .id(Ids.toLong(entity.getOrderItemId()))
                .orderId(entity.getOrderId())
                .productId(entity.getProductId())
                .quantity(entity.getQuantity())
                .price(entity.getPrice())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null)
                .build();
    }
}
