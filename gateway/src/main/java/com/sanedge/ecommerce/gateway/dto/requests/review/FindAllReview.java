package com.sanedge.ecommerce.gateway.dto.requests.review;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request untuk mengambil semua review dengan pagination dan pencarian")
public class FindAllReview {

    @NotBlank(message = "Kata kunci pencarian wajib diisi")
    @Parameter(description = "Kata kunci pencarian review", example = "produk bagus")
    private String search;

    @Min(value = 1, message = "Nomor halaman minimal 1")
    @Parameter(description = "Nomor halaman", example = "1")
    private int page = 1;

    @Min(value = 1, message = "Ukuran halaman minimal 1")
    @Max(value = 100, message = "Ukuran halaman maksimal 100")
    @Parameter(description = "Jumlah data per halaman (maksimal 100)", example = "20")
    private int pageSize = 20;
}