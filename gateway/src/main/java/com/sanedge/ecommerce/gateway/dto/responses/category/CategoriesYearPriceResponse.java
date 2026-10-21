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
public class CategoriesYearPriceResponse {
    private String year;
    private Long categoryId;
    private String categoryName;
    private Long orderCount;
    private Long itemsSold;
    private Long totalRevenue;
    private Long uniqueProductsSold;


}