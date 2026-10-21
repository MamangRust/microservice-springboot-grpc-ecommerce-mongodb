package com.sanedge.ecommerce.gateway.dto.responses.slider;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SliderResponse {
    private Long id;
    private String name;
    private String image;
    private String createdAt;
    private String updatedAt;

}