package com.sanedge.ecommerce.banner.domain.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindAllBannerRequest {

    @NotBlank
    private String search;

    @NotNull
    @Min(1)
    private Integer page;

    @NotNull
    @Min(1)
    @Max(100)
    private Integer pageSize;
}
