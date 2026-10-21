package com.sanedge.ecommerce.slider.domain.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSliderRequest {

    @NotNull
    private String nama;

    @NotNull
    private String filePath;
}
