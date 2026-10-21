package com.sanedge.ecommerce.gateway.dto.requests.review;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request untuk mengambil daftar review berdasarkan produk dengan filter")
public class FindAllReviewByProduct {

    @Parameter(description = "ID produk", example = "456", required = true)
    private Integer productId;

    @Min(value = 1, message = "Rating harus antara 1 sampai 5")
    @Max(value = 5, message = "Rating harus antara 1 sampai 5")
    @Parameter(description = "Filter berdasarkan rating (1-5)", example = "5")
    private Integer rating;

    @NotBlank(message = "Kata kunci pencarian wajib diisi")
    @Parameter(description = "Kata kunci pencarian review", example = "tahan lama")
    private String search;

    @Min(value = 1, message = "Nomor halaman minimal 1")
    @Parameter(description = "Nomor halaman", example = "1")
    private Integer page = 1;

    @Min(value = 1, message = "Ukuran halaman minimal 1")
    @Max(value = 100, message = "Ukuran halaman maksimal 100")
    @Parameter(description = "Jumlah data per halaman (maksimal 100)", example = "20")
    private Integer pageSize = 20;
}
