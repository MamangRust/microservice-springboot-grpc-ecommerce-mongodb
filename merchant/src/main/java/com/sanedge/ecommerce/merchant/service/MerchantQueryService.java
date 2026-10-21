package com.sanedge.ecommerce.merchant.service;

import java.util.List;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.merchant.domain.requests.FindAllMerchants;
import com.sanedge.ecommerce.merchant.domain.responses.MerchantResponse;
import com.sanedge.ecommerce.merchant.domain.responses.MerchantResponseDeleteAt;

public interface MerchantQueryService {
    ApiResponsePagination<List<MerchantResponse>> findAll(FindAllMerchants req);

    ApiResponsePagination<List<MerchantResponseDeleteAt>> findByActive(FindAllMerchants req);

    ApiResponsePagination<List<MerchantResponseDeleteAt>> findByTrashed(FindAllMerchants req);

    ApiResponse<MerchantResponse> findById(Integer merchantId);

    ApiResponse<MerchantResponse> findByUserId(Integer userId);
}
