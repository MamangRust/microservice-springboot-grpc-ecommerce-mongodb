package com.sanedge.ecommerce.merchant_policy.domain.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateMerchantPolicyRequest {

    @NotNull(message = "merchant_id wajib diisi")
    private Integer merchantId;

    @NotBlank(message = "policy_type wajib diisi")
    private String policyType;

    @NotBlank(message = "title wajib diisi")
    private String title;

    @NotBlank(message = "description wajib diisi")
    private String description;
}
