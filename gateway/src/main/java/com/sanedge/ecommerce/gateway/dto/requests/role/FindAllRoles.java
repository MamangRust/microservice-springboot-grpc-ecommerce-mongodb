package com.sanedge.ecommerce.gateway.dto.requests.role;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@Schema(description = "Request untuk mengambil daftar role dengan pagination dan pencarian")
public class FindAllRoles {

    @Parameter(description = "Nomor halaman", example = "1")
    @Min(value = 1, message = "Page minimal 1")
    private Integer page = 1;

    @Parameter(description = "Jumlah data per halaman", example = "10")
    @Min(value = 1, message = "Page size minimal 1")
    private Integer pageSize = 10;

    @Parameter(description = "Kata kunci pencarian berdasarkan nama role", example = "admin")
    private String search = "";
}