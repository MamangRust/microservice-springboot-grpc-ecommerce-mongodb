package com.sanedge.ecommerce.banner.service;

import com.sanedge.ecommerce.banner.domain.requests.CreateBannerRequest;
import com.sanedge.ecommerce.banner.domain.requests.UpdateBannerRequest;
import com.sanedge.ecommerce.banner.domain.responses.BannerResponse;
import com.sanedge.ecommerce.banner.domain.responses.BannerResponseDeleteAt;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;

public interface BannerCommandService {
    ApiResponse<BannerResponse> createBanner(CreateBannerRequest req);

    ApiResponse<BannerResponse> updateBanner(UpdateBannerRequest req);

    ApiResponse<BannerResponseDeleteAt> trashedBanner(Integer bannerId);

    ApiResponse<BannerResponseDeleteAt> restoreBanner(Integer bannerId);

    ApiResponse<Boolean> deleteBannerPermanent(Integer bannerId);

    ApiResponse<Boolean> restoreAllBanner();

    ApiResponse<Boolean> deleteAllBannerPermanent();
}
