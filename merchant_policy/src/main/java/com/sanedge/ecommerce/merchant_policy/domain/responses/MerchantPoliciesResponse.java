package com.sanedge.ecommerce.merchant_policy.domain.responses;

import com.sanedge.ecommerce.common.utils.Ids;
import com.sanedge.ecommerce.merchant_policy.models.MerchantPolicy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantPoliciesResponse {
    private Long id;
    private Integer merchantId;
    private String policyType;
    private String title;
    private String description;
    private String createdAt;
    private String updatedAt;

    public static MerchantPoliciesResponse from(MerchantPolicy entity) {
        return MerchantPoliciesResponse.builder()
                .id(Ids.toLong(entity.getMerchantPolicyId()))
                .merchantId(entity.getMerchantId())
                .policyType(entity.getPolicyType())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null)
                .build();
    }
}
