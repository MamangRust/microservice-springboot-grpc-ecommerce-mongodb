package com.sanedge.ecommerce.merchant_award.service;

import java.util.List;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.merchant_award.domain.requests.FindAllMerchantAwards;
import com.sanedge.ecommerce.merchant_award.domain.responses.MerchantAwardResponse;
import com.sanedge.ecommerce.merchant_award.domain.responses.MerchantAwardResponseDeleteAt;

public interface MerchantAwardQueryService {
    ApiResponsePagination<List<MerchantAwardResponse>> findAll(FindAllMerchantAwards req);

    ApiResponsePagination<List<MerchantAwardResponseDeleteAt>> findByActive(FindAllMerchantAwards req);

    ApiResponsePagination<List<MerchantAwardResponseDeleteAt>> findByTrashed(FindAllMerchantAwards req);

    ApiResponse<MerchantAwardResponse> findById(Integer merchantAwardId);
}
