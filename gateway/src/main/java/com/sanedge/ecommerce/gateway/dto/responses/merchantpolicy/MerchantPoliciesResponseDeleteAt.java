package com.sanedge.ecommerce.gateway.dto.responses.merchantpolicy;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantPoliciesResponseDeleteAt {
    private Long id;
    private Integer merchantId;
    private String policyType;
    private String title;
    private String description;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;

}