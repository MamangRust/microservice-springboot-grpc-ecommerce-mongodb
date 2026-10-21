package com.sanedge.ecommerce.gateway.dto.responses.category;

import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriesYearlyTotalPriceResponse {
    private String year;
    private Long totalRevenue;


}