package com.sanedge.ecommerce.merchant_policy.domain.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FindAllMerchantPolicies {

    @NotNull(message = "page wajib diisi")
    @Min(value = 1, message = "Page minimal 1")
    private Integer page;

    @NotNull(message = "page_size wajib diisi")
    @Min(value = 1, message = "Page size minimal 1")
    @Max(value = 100, message = "Page size maksimal 100")
    private Integer pageSize;

    private String search;
}
