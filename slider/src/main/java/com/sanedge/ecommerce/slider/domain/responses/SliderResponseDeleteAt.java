package com.sanedge.ecommerce.slider.domain.responses;

import com.sanedge.ecommerce.common.utils.Ids;
import com.sanedge.ecommerce.slider.models.Slider;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SliderResponseDeleteAt {
    private Long id;
    private String name;
    private String image;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;

    public static SliderResponseDeleteAt from(Slider entity) {
        return SliderResponseDeleteAt.builder()
                .id(Ids.toLong(entity.getSliderId()))
                .name(entity.getName())
                .image(entity.getImage())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null)
                .deletedAt(entity.getDeletedAt() != null ? entity.getDeletedAt().toString() : null)
                .build();
    }
}
