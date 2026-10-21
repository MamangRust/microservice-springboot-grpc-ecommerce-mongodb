package com.sanedge.ecommerce.auth.domain.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthRequest {

    @Email(message = "Email tidak valid")
    @NotBlank(message = "Email wajib diisi")
    private String email;

    @Size(min = 6, message = "Password minimal 6 karakter")
    @NotBlank(message = "Password wajib diisi")
    private String password;
}
