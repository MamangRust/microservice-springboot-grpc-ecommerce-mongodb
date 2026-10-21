package com.sanedge.ecommerce.gateway.dto.requests.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request untuk filter total harga bulanan berdasarkan merchant")
public class MonthTotalPriceMerchantRequest {

    @NotNull
    @Schema(description = "ID merchant untuk filter", example = "5")
    private Integer merchantId;

    @NotNull
    @Schema(description = "Tahun untuk filter", example = "2024")
    private Integer year;

    @NotNull
    @Schema(description = "Bulan untuk filter", example = "9")
    private Integer month;
}
