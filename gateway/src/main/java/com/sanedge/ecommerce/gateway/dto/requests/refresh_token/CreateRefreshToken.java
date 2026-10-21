package com.sanedge.ecommerce.gateway.dto.requests.refresh_token;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request untuk membuat refresh token")
public class CreateRefreshToken {
    @Min(value = 1, message = "User ID wajib >= 1")
    private Integer userId;

    @NotBlank(message = "Token wajib diisi")
    private String token;

    @NotBlank(message = "ExpiresAt wajib diisi")
    private String expiresAt;
}