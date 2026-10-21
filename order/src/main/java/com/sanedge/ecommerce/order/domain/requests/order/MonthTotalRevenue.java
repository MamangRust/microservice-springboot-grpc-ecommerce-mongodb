package com.sanedge.ecommerce.order.domain.requests.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MonthTotalRevenue {
    @NotNull
    private Integer year;

    @NotNull
    private Integer month;
}
