package com.sanedge.ecommerce.merchant_business.service;

import java.util.List;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.merchant_business.domain.requests.FindAllMerchantRequest;
import com.sanedge.ecommerce.merchant_business.domain.responses.MerchantBusinessResponse;
import com.sanedge.ecommerce.merchant_business.domain.responses.MerchantBusinessResponseDeleteAt;

public interface MerchantBusinessQueryService {
    ApiResponsePagination<List<MerchantBusinessResponse>> findAll(FindAllMerchantRequest req);

    ApiResponsePagination<List<MerchantBusinessResponseDeleteAt>> findByActive(FindAllMerchantRequest req);

    ApiResponsePagination<List<MerchantBusinessResponseDeleteAt>> findByTrashed(FindAllMerchantRequest req);

    ApiResponse<MerchantBusinessResponse> findById(Integer merchantBusinessInfoId);
}
