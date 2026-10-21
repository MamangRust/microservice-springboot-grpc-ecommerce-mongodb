package com.sanedge.ecommerce.slider.domain.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FindAllSliderRequest {

    @NotNull
    private String search;

    @Min(1)
    private int page = 1;

    @Min(1)
    private int pageSize = 10;
}
