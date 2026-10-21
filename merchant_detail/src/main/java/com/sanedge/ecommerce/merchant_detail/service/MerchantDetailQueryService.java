package com.sanedge.ecommerce.merchant_detail.service;

import java.util.List;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.merchant_detail.domain.requests.FindAllMerchantDetailRequest;
import com.sanedge.ecommerce.merchant_detail.domain.responses.MerchantDetailRelationResponse;
import com.sanedge.ecommerce.merchant_detail.domain.responses.MerchantDetailRelationResponseDeleteAt;

public interface MerchantDetailQueryService {
    ApiResponsePagination<List<MerchantDetailRelationResponse>> findAll(FindAllMerchantDetailRequest req);

    ApiResponsePagination<List<MerchantDetailRelationResponseDeleteAt>> findByActive(FindAllMerchantDetailRequest req);

    ApiResponsePagination<List<MerchantDetailRelationResponseDeleteAt>> findByTrashed(FindAllMerchantDetailRequest req);

    ApiResponse<MerchantDetailRelationResponse> findById(Integer merchantID);
}
