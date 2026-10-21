package com.sanedge.ecommerce.merchant_detail.service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.merchant_detail.domain.requests.CreateMerchantDetailRequest;
import com.sanedge.ecommerce.merchant_detail.domain.requests.UpdateMerchantDetailRequest;
import com.sanedge.ecommerce.merchant_detail.domain.responses.MerchantDetailResponse;
import com.sanedge.ecommerce.merchant_detail.domain.responses.MerchantDetailResponseDeleteAt;

public interface MerchantDetailCommandService {
    ApiResponse<MerchantDetailResponse> createMerchant(CreateMerchantDetailRequest req);

    ApiResponse<MerchantDetailResponse> updateMerchant(UpdateMerchantDetailRequest req);

    ApiResponse<MerchantDetailResponseDeleteAt> trashedMerchant(Integer merchantID);

    ApiResponse<MerchantDetailResponseDeleteAt> restoreMerchant(Integer merchantID);

    ApiResponse<Boolean> deleteMerchantPermanent(Integer merchantID);

    ApiResponse<Boolean> restoreAllMerchant();

    ApiResponse<Boolean> deleteAllMerchantPermanent();
}
