package com.sanedge.ecommerce.merchant_social_link.service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.merchant_social_link.domain.requests.CreateMerchantSocialRequest;
import com.sanedge.ecommerce.merchant_social_link.domain.requests.UpdateMerchantSocialRequest;
import com.sanedge.ecommerce.merchant_social_link.domain.responses.MerchantSocialMediaLinkResponse;
import com.sanedge.ecommerce.merchant_social_link.domain.responses.MerchantSocialMediaLinkResponseDeleteAt;

public interface MerchantSocialLinkService {
    ApiResponse<MerchantSocialMediaLinkResponse> create(CreateMerchantSocialRequest merchantSocialRequest);

    ApiResponse<MerchantSocialMediaLinkResponse> update(UpdateMerchantSocialRequest merchantSocialRequest);

    ApiResponse<MerchantSocialMediaLinkResponseDeleteAt> trash(Integer id);

    ApiResponse<MerchantSocialMediaLinkResponseDeleteAt> restore(Integer id);

    ApiResponse<Boolean> delete(Integer id);

    ApiResponse<Boolean> restoreAll();

    ApiResponse<Boolean> deleteAll();
}
