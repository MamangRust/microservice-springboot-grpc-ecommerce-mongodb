package com.sanedge.ecommerce.gateway.dto.requests.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request untuk membuat pengguna baru")
public class CreateUserRequest {

    @NotBlank(message = "Username wajib diisi")
    @Schema(description = "Username pengguna baru", example = "johndoe")
    private String username;

    @NotBlank(message = "Nama depan wajib diisi")
    @Schema(description = "Nama depan pengguna baru", example = "John")
    private String firstname;

    @NotBlank(message = "Nama belakang wajib diisi")
    @Schema(description = "Nama belakang pengguna baru", example = "Doe")
    private String lastname;

    @Email(message = "Format email tidak valid")
    @Schema(description = "Email pengguna baru", example = "john.doe@email.com")
    private String email;

    @Size(min = 6, message = "Password minimal 6 karakter")
    @Schema(description = "Password pengguna baru", example = "secret123")
    private String password;

    @Size(min = 6, message = "Konfirmasi password minimal 6 karakter")
    @Schema(description = "Konfirmasi password pengguna baru", example = "secret123")
    private String confirmPassword;
}
