package com.sanedge.ecommerce.auth.domain.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token wajib diisi")
    private String refreshToken;
}
