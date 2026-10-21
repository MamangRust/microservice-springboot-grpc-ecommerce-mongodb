package com.sanedge.ecommerce.category.domain.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import lombok.Data;

@Data
public class UpdateCategoryRequest {

    @Null
    private Integer categoryId;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotBlank
    private String slugCategory;

    private String imageCategory;
}
