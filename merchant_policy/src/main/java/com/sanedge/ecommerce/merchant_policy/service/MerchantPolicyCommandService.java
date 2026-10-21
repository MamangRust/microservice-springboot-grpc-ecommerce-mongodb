package com.sanedge.ecommerce.merchant_policy.service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.merchant_policy.domain.requests.CreateMerchantPolicyRequest;
import com.sanedge.ecommerce.merchant_policy.domain.requests.UpdateMerchantPolicyRequest;
import com.sanedge.ecommerce.merchant_policy.domain.responses.MerchantPoliciesResponse;
import com.sanedge.ecommerce.merchant_policy.domain.responses.MerchantPoliciesResponseDeleteAt;

public interface MerchantPolicyCommandService {
    ApiResponse<MerchantPoliciesResponse> create(CreateMerchantPolicyRequest request);

    ApiResponse<MerchantPoliciesResponse> update(UpdateMerchantPolicyRequest request);

    ApiResponse<MerchantPoliciesResponseDeleteAt> trash(Integer id);

    ApiResponse<MerchantPoliciesResponseDeleteAt> restore(Integer id);

    ApiResponse<Boolean> delete(Integer id);

    ApiResponse<Boolean> restoreAll();

    ApiResponse<Boolean> deleteAll();
}
