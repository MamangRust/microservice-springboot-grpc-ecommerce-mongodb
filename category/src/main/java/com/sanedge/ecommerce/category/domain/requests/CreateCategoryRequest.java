package com.sanedge.ecommerce.category.domain.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCategoryRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotBlank
    private String slugCategory;

    private String imageCategory;
}
