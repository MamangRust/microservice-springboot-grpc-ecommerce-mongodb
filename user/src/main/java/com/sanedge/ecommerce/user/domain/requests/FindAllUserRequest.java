package com.sanedge.ecommerce.user.domain.requests;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class FindAllUserRequest {

    @Min(value = 1, message = "Halaman minimal 1")
    private Integer page = 1;

    @Min(value = 1, message = "Jumlah data per halaman minimal 1")
    private Integer pageSize = 10;

    private String search = "";
}
