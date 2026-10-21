package com.sanedge.ecommerce.user.domain.responses;

import com.sanedge.ecommerce.common.utils.Ids;
import com.sanedge.ecommerce.user.models.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for the FindByEmail gRPC contract, which carries the stored
 * password hash (consumed by the auth service to verify credentials).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseWithPassword {
    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String createdAt;
    private String updatedAt;

    public static UserResponseWithPassword from(User user) {
        Long id = Ids.toLong(user.getUserId());
        return UserResponseWithPassword.builder()
                .id(id != null ? id.intValue() : null)
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .password(user.getPassword())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)
                .updatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null)
                .build();
    }
}
