package com.sanedge.ecommerce.merchant_business.domain.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class FindAllMerchantRequest {

    private String search = "";

    @Min(value = 1, message = "Page minimal 1")
    private Integer page = 1;

    @Min(value = 1, message = "Page size minimal 1")
    @Max(value = 100, message = "Page size maksimal 100")
    private Integer pageSize = 10;
}
