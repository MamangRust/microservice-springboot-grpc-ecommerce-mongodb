package com.sanedge.ecommerce.category.domain.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthPriceMerchantRequest {
    @NotNull
    private Integer merchantId;

    @NotNull
    private Integer year;
}
