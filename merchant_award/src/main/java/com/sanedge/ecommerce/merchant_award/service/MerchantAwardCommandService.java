package com.sanedge.ecommerce.merchant_award.service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.merchant_award.domain.requests.CreateMerchantAwardRequest;
import com.sanedge.ecommerce.merchant_award.domain.requests.UpdateMerchantAwardRequest;
import com.sanedge.ecommerce.merchant_award.domain.responses.MerchantAwardResponse;
import com.sanedge.ecommerce.merchant_award.domain.responses.MerchantAwardResponseDeleteAt;

public interface MerchantAwardCommandService {
    ApiResponse<MerchantAwardResponse> createMerchantAward(CreateMerchantAwardRequest req);

    ApiResponse<MerchantAwardResponse> updateMerchantAward(UpdateMerchantAwardRequest req);

    ApiResponse<MerchantAwardResponseDeleteAt> trashedMerchantAward(Integer merchantAwardId);

    ApiResponse<MerchantAwardResponseDeleteAt> restoreMerchantAward(Integer merchantAwardId);

    ApiResponse<Boolean> deleteMerchantAwardPermanent(Integer merchantAwardId);

    ApiResponse<Boolean> restoreAllMerchantAward();

    ApiResponse<Boolean> deleteAllMerchantAwardPermanent();
}
