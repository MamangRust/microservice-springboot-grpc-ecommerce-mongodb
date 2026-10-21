package com.sanedge.ecommerce.gateway.dto.requests.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request untuk membuat role baru")
public class CreateRoleRequest {

    @NotBlank(message = "Nama role wajib diisi")
    @Schema(description = "Nama role yang akan dibuat", example = "ADMIN", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
}