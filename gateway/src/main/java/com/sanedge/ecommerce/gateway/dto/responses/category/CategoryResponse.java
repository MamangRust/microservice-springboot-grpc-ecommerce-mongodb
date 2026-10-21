package com.sanedge.ecommerce.gateway.dto.responses.category;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    private Long id;
    private String name;
    private String description;
    private String slugCategory;
    private String imageCategory;
    private String createdAt;
    private String updatedAt;

}