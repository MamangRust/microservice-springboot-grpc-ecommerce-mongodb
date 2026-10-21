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
public class OrderYearlyResponse {
    private String year;
    private Integer orderCount;
    private Long totalRevenue;
    private Integer totalItemsSold;
    private Integer activeCashiers;
    private Integer uniqueProductsSold;


}