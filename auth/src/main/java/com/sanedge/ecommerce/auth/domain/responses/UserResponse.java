package com.sanedge.ecommerce.auth.domain.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private String createdAt;
    private String updatedAt;

    public static UserResponse from(com.sanedge.ecommerce.pb.user.UserResponse u) {
        return UserResponse.builder()
                .id(u.getId())
                .firstname(u.getFirstname())
                .lastname(u.getLastname())
                .email(u.getEmail())
                .createdAt(u.getCreatedAt())
                .updatedAt(u.getUpdatedAt())
                .build();
    }

    public static UserResponse from(com.sanedge.ecommerce.common.adapter.model.User u) {
        return UserResponse.builder()
                .id(u.id())
                .firstname(u.firstname())
                .lastname(u.lastname())
                .email(u.email())
                .createdAt(u.createdAt() != null ? u.createdAt().toString() : null)
                .updatedAt(u.updatedAt() != null ? u.updatedAt().toString() : null)
                .build();
    }
}
