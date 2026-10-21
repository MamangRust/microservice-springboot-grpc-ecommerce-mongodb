package com.sanedge.ecommerce.user.domain.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank(message = "Username wajib diisi")
    private String username;

    @NotBlank(message = "Nama depan wajib diisi")
    private String firstname;

    @NotBlank(message = "Nama belakang wajib diisi")
    private String lastname;

    @Email(message = "Format email tidak valid")
    private String email;

    @Size(min = 6, message = "Password minimal 6 karakter")
    private String password;

    @Size(min = 6, message = "Konfirmasi password minimal 6 karakter")
    private String confirmPassword;
}
