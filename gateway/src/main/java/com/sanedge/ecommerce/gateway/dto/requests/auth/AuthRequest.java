package com.sanedge.ecommerce.gateway.dto.requests.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request untuk login user menggunakan username")
public class AuthRequest {

    @NotBlank(message = "Username wajib diisi")
    @Schema(example = "johndoe")
    private String username;

    @Size(min = 6, message = "Password minimal 6 karakter")
    @NotBlank(message = "Password wajib diisi")
    @Schema(example = "password123")
    private String password;
}