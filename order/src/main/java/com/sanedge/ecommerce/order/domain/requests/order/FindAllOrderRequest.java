package com.sanedge.ecommerce.order.domain.requests.order;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class FindAllOrderRequest {

    private String search;

    @Min(1)
    private int page = 1;

    @Min(1)
    private int pageSize = 10;
}
