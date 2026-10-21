package com.sanedge.ecommerce.gateway.dto.requests.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request untuk memperbarui data pengguna yang sudah ada")
public class UpdateUserRequest {

    @Min(value = 1, message = "ID harus lebih besar dari 0")
    @Schema(description = "ID pengguna yang akan diperbarui", example = "1")
    private Integer id;

    @Schema(description = "Username pengguna", example = "johndoe")
    private String username;

    @Schema(description = "Nama depan pengguna", example = "John")
    private String firstname;

    @Schema(description = "Nama belakang pengguna", example = "Doe")
    private String lastname;

    @Email(message = "Format email tidak valid")
    @Schema(description = "Email pengguna", example = "john.doe@email.com")
    private String email;

    @Size(min = 6, message = "Password minimal 6 karakter")
    @Schema(description = "Password baru pengguna", example = "secret123")
    private String password;

    @Size(min = 6, message = "Konfirmasi password minimal 6 karakter")
    @Schema(description = "Konfirmasi password baru", example = "secret123")
    private String confirmPassword;
}