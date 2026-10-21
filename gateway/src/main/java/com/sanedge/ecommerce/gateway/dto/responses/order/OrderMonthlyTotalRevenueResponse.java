package com.sanedge.ecommerce.gateway.dto.responses.order;

import java.util.List;


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


}