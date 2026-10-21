package com.sanedge.ecommerce.gateway.dto.requests.banner;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request untuk mendapatkan daftar banner dengan pagination dan search")
public class FindAllBannerRequest {

    @NotBlank
    @Parameter(description = "Kata kunci pencarian banner", example = "Promo")
    private String search;

    @NotNull
    @Min(1)
    @Parameter(description = "Nomor halaman", example = "1")
    private Integer page;

    @NotNull
    @Min(1)
    @Max(100)
    @Parameter(description = "Jumlah data per halaman", example = "10")
    private Integer pageSize;
}
