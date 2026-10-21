package com.sanedge.ecommerce.gateway.dto.requests.shipping;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@Schema(description = "Request untuk mengambil daftar alamat pengiriman dengan pagination dan pencarian")
public class FindAllShippingAddress {

    @Parameter(description = "Nomor halaman", example = "1")
    @Min(value = 1, message = "Page minimal 1")
    private Integer page = 1;

    @Parameter(description = "Jumlah data per halaman", example = "10")
    @Min(value = 1, message = "Page size minimal 1")
    private Integer pageSize = 10;

    @Parameter(description = "Pencarian berdasarkan nama shipping", example = "Rumah Utama")
    private String search = "";
}