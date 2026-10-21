package com.sanedge.ecommerce.gateway.dto.requests.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request untuk filter total harga bulanan (umum, tanpa kategori/merchant)")
public class MonthTotalPriceRequest {

    @NotNull
    @Schema(description = "Tahun untuk filter", example = "2024")
    private Integer year;

    @NotNull
    @Schema(description = "Bulan untuk filter", example = "9")
    private Integer month;
}
