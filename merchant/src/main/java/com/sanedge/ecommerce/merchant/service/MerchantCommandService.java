package com.sanedge.ecommerce.merchant.service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.merchant.domain.requests.CreateMerchantRequest;
import com.sanedge.ecommerce.merchant.domain.requests.UpdateMerchantRequest;
import com.sanedge.ecommerce.merchant.domain.responses.MerchantResponse;
import com.sanedge.ecommerce.merchant.domain.responses.MerchantResponseDeleteAt;

public interface MerchantCommandService {
    ApiResponse<MerchantResponse> createMerchant(CreateMerchantRequest req);

    ApiResponse<MerchantResponse> updateMerchant(UpdateMerchantRequest req);

    ApiResponse<MerchantResponse> updateMerchantStatus(Integer merchantId, String status);

    ApiResponse<MerchantResponseDeleteAt> trashedMerchant(Integer merchantId);

    ApiResponse<MerchantResponseDeleteAt> restoreMerchant(Integer merchantId);

    ApiResponse<Boolean> deleteMerchantPermanent(Integer merchantId);

    ApiResponse<Boolean> restoreAllMerchant();

    ApiResponse<Boolean> deleteAllMerchantPermanent();
}
