package com.sanedge.ecommerce.gateway.dto.requests.refresh_token;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request untuk refresh token baru")
public class RefreshTokenRequest {
    @NotBlank(message = "Refresh token wajib diisi")
    @Schema(description = "Refresh token", example = "helloworld")
    private String refreshToken;
}