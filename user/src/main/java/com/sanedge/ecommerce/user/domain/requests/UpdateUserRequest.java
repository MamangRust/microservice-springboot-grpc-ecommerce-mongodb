package com.sanedge.ecommerce.user.domain.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @Min(value = 1, message = "ID harus lebih besar dari 0")
    private Integer id;

    private String username;

    private String firstname;

    private String lastname;

    @Email(message = "Format email tidak valid")
    private String email;

    @Size(min = 6, message = "Password minimal 6 karakter")
    private String password;

    @Size(min = 6, message = "Konfirmasi password minimal 6 karakter")
    private String confirmPassword;
}
