package com.sanedge.ecommerce.order.domain.responses.order;

import java.util.List;

import com.sanedge.ecommerce.order.models.OrderMonthly;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderMonthlyResponse {
    private String month;
    private Integer orderCount;
    private Long totalRevenue;
    private Integer totalItemsSold;

    public static OrderMonthlyResponse from(OrderMonthly response) {
        return OrderMonthlyResponse.builder()
                .month(response.getMonth())
                .orderCount(response.getOrderCount())
                .totalRevenue(response.getTotalRevenue())
                .totalItemsSold(response.getTotalItemsSold())
                .build();
    }

    public static List<OrderMonthlyResponse> fromList(List<OrderMonthly> responses) {
        if (responses == null)
            return List.of();
        return responses.stream().map(OrderMonthlyResponse::from).toList();
    }
}
