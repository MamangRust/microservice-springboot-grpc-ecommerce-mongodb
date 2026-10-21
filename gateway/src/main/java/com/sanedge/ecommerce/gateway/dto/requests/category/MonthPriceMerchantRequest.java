package com.sanedge.ecommerce.gateway.dto.requests.category;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Merchant ID for filtering", example = "5")
    private Integer merchantId;

    @NotNull
    @Schema(description = "Year for filtering", example = "2024")
    private Integer year;
}
