package com.sanedge.ecommerce.slider.service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.slider.domain.requests.CreateSliderRequest;
import com.sanedge.ecommerce.slider.domain.requests.UpdateSliderRequest;
import com.sanedge.ecommerce.slider.domain.responses.SliderResponse;
import com.sanedge.ecommerce.slider.domain.responses.SliderResponseDeleteAt;

public interface SliderCommandService {
    ApiResponse<SliderResponse> createSlider(CreateSliderRequest req);

    ApiResponse<SliderResponse> updateSlider(UpdateSliderRequest req);

    ApiResponse<SliderResponseDeleteAt> trashedSlider(Integer sliderId);

    ApiResponse<SliderResponseDeleteAt> restoreSlider(Integer sliderId);

    ApiResponse<Boolean> deleteSliderPermanent(Integer sliderId);

    ApiResponse<Boolean> restoreAllSliders();

    ApiResponse<Boolean> deleteAllSlidersPermanent();
}
