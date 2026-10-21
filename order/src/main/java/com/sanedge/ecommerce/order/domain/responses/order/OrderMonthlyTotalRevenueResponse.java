package com.sanedge.ecommerce.order.domain.responses.order;

import java.util.List;

import com.sanedge.ecommerce.order.models.OrderMonthlyTotalRevenue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderMonthlyTotalRevenueResponse {
    private String year;
    private String month;
    private Long totalRevenue;

    public static OrderMonthlyTotalRevenueResponse from(OrderMonthlyTotalRevenue response) {
        return OrderMonthlyTotalRevenueResponse.builder()
                .year(response.getYear())
                .month(response.getMonth())
                .totalRevenue(response.getTotalRevenue())
                .build();
    }

    public static List<OrderMonthlyTotalRevenueResponse> fromList(List<OrderMonthlyTotalRevenue> responses) {
        if (responses == null)
            return List.of();
        return responses.stream().map(OrderMonthlyTotalRevenueResponse::from).toList();
    }
}
