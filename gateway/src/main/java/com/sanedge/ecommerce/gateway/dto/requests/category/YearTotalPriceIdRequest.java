package com.sanedge.ecommerce.gateway.dto.requests.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request untuk filter total harga tahunan berdasarkan kategori ID")
public class YearTotalPriceIdRequest {

    @Schema(description = "ID kategori untuk filter", example = "12")
    private Integer categoryId;

    @NotNull
    @Schema(description = "Tahun untuk filter", example = "2024")
    private Integer year;
}
