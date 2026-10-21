package com.sanedge.ecommerce.category.domain.responses;

import com.sanedge.ecommerce.category.models.Category;
import com.sanedge.ecommerce.common.utils.Ids;

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

    public static CategoryResponse from(Category category) {
        return CategoryResponse.builder()
                .id(Ids.toLong(category.getCategoryId()))
                .name(category.getName())
                .description(category.getDescription())
                .slugCategory(category.getSlugCategory())
                .imageCategory(category.getImageCategory())
                .createdAt(category.getCreatedAt() != null ? category.getCreatedAt().toString() : null)
                .updatedAt(category.getUpdatedAt() != null ? category.getUpdatedAt().toString() : null)
                .build();
    }
}
