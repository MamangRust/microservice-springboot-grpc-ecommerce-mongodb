package com.sanedge.ecommerce.gateway.dto.responses.role;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponseDeleteAt {
    private Integer id;
    private String name;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;

}