package com.sanedge.ecommerce.gateway.dto.requests.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
@Schema(description = "Request untuk filter harga tahunan berdasarkan merchant")
public class YearPriceMerchantRequest {

    @NotNull
    @Schema(description = "ID merchant untuk filter", example = "5")
    private Integer merchantId;

    @NotNull
    @Schema(description = "Tahun untuk filter", example = "2024")
    private Integer year;
}
