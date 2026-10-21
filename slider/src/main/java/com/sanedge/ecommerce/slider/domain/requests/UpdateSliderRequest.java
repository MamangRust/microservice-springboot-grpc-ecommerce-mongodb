package com.sanedge.ecommerce.slider.domain.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateSliderRequest {

    @NotNull
    private Integer id;

    @NotNull
    private String nama;

    private String filePath;
}
