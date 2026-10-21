package com.sanedge.ecommerce.gateway.dto.requests.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request untuk registrasi user")
public class RegisterRequest {

    @Size(min = 2, message = "First name must be at least 2 characters")
    @NotBlank(message = "First name wajib diisi")
    @Schema(example = "John")
    private String firstname;

    @NotBlank(message = "Username wajib diisi")
    @Schema(example = "johndoe")
    private String username;

    @Size(min = 2, message = "Last name must be at least 2 characters")
    @NotBlank(message = "Last name wajib diisi")
    @Schema(example = "Doe")
    private String lastname;

    @Email(message = "Email tidak valid")
    @NotBlank(message = "Email wajib diisi")
    @Schema(example = "john.doe@example.com")
    private String email;

    @Size(min = 6, message = "Password minimal 6 karakter")
    @NotBlank(message = "Password wajib diisi")
    @Schema(example = "password123")
    private String password;

    @Size(min = 6, message = "Confirm password minimal 6 karakter")
    @NotBlank(message = "Confirm password wajib diisi")
    @Schema(example = "password123")
    private String confirmPassword;
}