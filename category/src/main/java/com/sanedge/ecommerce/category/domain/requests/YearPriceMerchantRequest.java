package com.sanedge.ecommerce.category.domain.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class YearPriceMerchantRequest {

    @NotNull
    private Integer merchantId;

    @NotNull
    private Integer year;
}
