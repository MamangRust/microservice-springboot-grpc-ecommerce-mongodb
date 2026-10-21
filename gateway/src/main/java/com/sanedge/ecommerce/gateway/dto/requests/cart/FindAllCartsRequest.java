package com.sanedge.ecommerce.gateway.dto.requests.cart;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request untuk mendapatkan daftar cart dengan pagination dan search")
public class FindAllCartsRequest {

    @NotNull
    @Parameter(description = "ID pengguna", example = "123")
    private Integer userId;

    @NotBlank
    @Parameter(description = "Kata kunci pencarian produk di cart", example = "Laptop")
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