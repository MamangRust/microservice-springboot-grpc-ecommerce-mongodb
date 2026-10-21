package com.sanedge.ecommerce.cart.domain.requests;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class DeleteCartRequest {

    @NotEmpty
    private List<Integer> cartIds;
}
