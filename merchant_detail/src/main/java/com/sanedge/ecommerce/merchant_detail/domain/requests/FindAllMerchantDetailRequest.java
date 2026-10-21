package com.sanedge.ecommerce.merchant_detail.domain.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindAllMerchantDetailRequest {
    private String search;
    private Integer page;
    private Integer pageSize;
}
