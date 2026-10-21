package com.sanedge.ecommerce.merchant_policy.service;

import java.util.List;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.merchant_policy.domain.requests.FindAllMerchantPolicies;
import com.sanedge.ecommerce.merchant_policy.domain.responses.MerchantPoliciesResponse;
import com.sanedge.ecommerce.merchant_policy.domain.responses.MerchantPoliciesResponseDeleteAt;

public interface MerchantPolicyQueryService {
    ApiResponsePagination<List<MerchantPoliciesResponse>> findAll(FindAllMerchantPolicies req);

    ApiResponsePagination<List<MerchantPoliciesResponseDeleteAt>> findByActive(FindAllMerchantPolicies req);

    ApiResponsePagination<List<MerchantPoliciesResponseDeleteAt>> findByTrashed(FindAllMerchantPolicies req);

    ApiResponse<MerchantPoliciesResponse> findById(Integer id);
}
