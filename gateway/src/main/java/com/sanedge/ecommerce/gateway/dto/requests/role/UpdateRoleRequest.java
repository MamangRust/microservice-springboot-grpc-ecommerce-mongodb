package com.sanedge.ecommerce.gateway.dto.requests.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request untuk memperbarui role")
public class UpdateRoleRequest {

    @Min(value = 1, message = "ID role minimal 1")
    @Schema(description = "ID role yang akan diperbarui", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer id;

    @NotBlank(message = "Nama role wajib diisi")
    @Schema(description = "Nama role baru untuk diperbarui", example = "SUPER_ADMIN", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
}