package com.sanedge.ecommerce.category.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriesMonthlyTotalPrice {
    private String year;
    private String month;
    private Long totalRevenue;
}
