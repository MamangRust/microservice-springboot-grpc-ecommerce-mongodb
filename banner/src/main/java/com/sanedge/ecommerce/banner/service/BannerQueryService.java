package com.sanedge.ecommerce.banner.service;

import java.util.List;

import com.sanedge.ecommerce.banner.domain.requests.FindAllBannerRequest;
import com.sanedge.ecommerce.banner.domain.responses.BannerResponse;
import com.sanedge.ecommerce.banner.domain.responses.BannerResponseDeleteAt;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;

public interface BannerQueryService {
    ApiResponsePagination<List<BannerResponse>> findAll(FindAllBannerRequest req);

    ApiResponsePagination<List<BannerResponseDeleteAt>> findByActive(FindAllBannerRequest req);

    ApiResponsePagination<List<BannerResponseDeleteAt>> findByTrashed(FindAllBannerRequest req);

    ApiResponse<BannerResponse> findById(Integer bannerId);
}
