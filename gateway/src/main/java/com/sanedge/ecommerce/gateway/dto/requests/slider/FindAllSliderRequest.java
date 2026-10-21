package com.sanedge.ecommerce.gateway.dto.requests.slider;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request untuk mengambil semua slider dengan paging dan filter search")
public class FindAllSliderRequest {

    @NotNull
    @Parameter(description = "Keyword pencarian slider", example = "promo")
    private String search;

    @Min(1)
    @Parameter(description = "Nomor halaman", example = "1")
    private int page = 1;

    @Min(1)
    @Parameter(description = "Jumlah data per halaman", example = "10")
    private int pageSize = 10;
}