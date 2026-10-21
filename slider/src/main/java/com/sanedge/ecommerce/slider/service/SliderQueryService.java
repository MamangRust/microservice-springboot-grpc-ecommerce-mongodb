package com.sanedge.ecommerce.slider.service;

import java.util.List;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.slider.domain.requests.FindAllSliderRequest;
import com.sanedge.ecommerce.slider.domain.responses.SliderResponse;
import com.sanedge.ecommerce.slider.domain.responses.SliderResponseDeleteAt;

public interface SliderQueryService {
    ApiResponsePagination<List<SliderResponse>> findAll(FindAllSliderRequest req);

    ApiResponsePagination<List<SliderResponseDeleteAt>> findByActive(FindAllSliderRequest req);

    ApiResponsePagination<List<SliderResponseDeleteAt>> findByTrashed(FindAllSliderRequest req);

    ApiResponse<SliderResponse> findById(Integer id);
}
