package com.sanedge.ecommerce.gateway.dto.requests.user;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@Schema(description = "Request untuk mengambil daftar pengguna dengan filter pagination dan pencarian")
public class FindAllUserRequest {

    @Min(value = 1, message = "Halaman minimal 1")
    @Parameter(description = "Halaman yang akan diambil", example = "1")
    private Integer page = 1;

    @Min(value = 1, message = "Jumlah data per halaman minimal 1")
    @Parameter(description = "Jumlah data per halaman", example = "10")
    private Integer pageSize = 10;

    @Parameter(description = "Kata kunci pencarian (nama/email/field lain)", example = "john")
    private String search = "";
}
