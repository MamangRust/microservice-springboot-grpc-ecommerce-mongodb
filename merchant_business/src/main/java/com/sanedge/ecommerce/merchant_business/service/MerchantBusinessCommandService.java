package com.sanedge.ecommerce.merchant_business.service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.merchant_business.domain.requests.CreateMerchantBusinessRequest;
import com.sanedge.ecommerce.merchant_business.domain.requests.UpdateMerchantBusinessRequest;
import com.sanedge.ecommerce.merchant_business.domain.responses.MerchantBusinessResponse;
import com.sanedge.ecommerce.merchant_business.domain.responses.MerchantBusinessResponseDeleteAt;

public interface MerchantBusinessCommandService {
    ApiResponse<MerchantBusinessResponse> createMerchantBusiness(CreateMerchantBusinessRequest req);

    ApiResponse<MerchantBusinessResponse> updateMerchantBusiness(UpdateMerchantBusinessRequest req);

    ApiResponse<MerchantBusinessResponseDeleteAt> trashedMerchantBusiness(Integer merchantBusinessInfoId);

    ApiResponse<MerchantBusinessResponseDeleteAt> restoreMerchantBusiness(Integer merchantBusinessInfoId);

    ApiResponse<Boolean> deleteMerchantBusinessPermanent(Integer merchantBusinessInfoId);

    ApiResponse<Boolean> restoreAllMerchantBusiness();

    ApiResponse<Boolean> deleteAllMerchantBusinessPermanent();
}
