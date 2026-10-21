package com.sanedge.ecommerce.review.domain.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FindAllReview {

    @NotBlank(message = "Kata kunci pencarian wajib diisi")
    private String search;

    @Min(value = 1, message = "Nomor halaman minimal 1")
    private int page = 1;

    @Min(value = 1, message = "Ukuran halaman minimal 1")
    @Max(value = 100, message = "Ukuran halaman maksimal 100")
    private int pageSize = 20;
}
