package com.sanedge.ecommerce.slider.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.domain.responses.api.PaginationMeta;
import com.sanedge.ecommerce.common.observability.TracingMetrics;
import com.sanedge.ecommerce.slider.domain.requests.FindAllSliderRequest;
import com.sanedge.ecommerce.slider.domain.responses.SliderResponse;
import com.sanedge.ecommerce.slider.domain.responses.SliderResponseDeleteAt;
import com.sanedge.ecommerce.slider.models.Slider;
import com.sanedge.ecommerce.slider.repository.SliderQueryRepository;
import com.sanedge.ecommerce.slider.service.SliderQueryService;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class SliderQueryImplService implements SliderQueryService {

        private final SliderQueryRepository sliderQueryRepository;
        private final TracingMetrics tracingMetrics;

        @Override
        public ApiResponsePagination<List<SliderResponse>> findAll(FindAllSliderRequest req) {
                Attributes attrs = Attributes.builder()
                                .put("slider.page", String.valueOf(req.getPage()))
                                .put("slider.size", String.valueOf(req.getPageSize()))
                                .build();

                return tracingMetrics.traceAndMeasure("findAllSliders", "find_all_sliders", attrs, () -> {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                        log.info("🔍 Searching sliders | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                        keyword.isEmpty() ? "None" : keyword);

                        try {
                                Pageable pageable = PageRequest.of(page, pageSize);
                                Page<Slider> sliderPage = sliderQueryRepository.findSliders(keyword, pageable);

                                List<SliderResponse> responses = sliderPage.getContent()
                                                .stream()
                                                .map(SliderResponse::from)
                                                .toList();

                                log.info("✅ Found {} sliders", responses.size());

                                return ApiResponsePagination.<List<SliderResponse>>builder()
                                                .status("success")
                                                .message("Sliders retrieved successfully")
                                                .data(responses)
                                                .pagination(PaginationMeta.fromSpringPage(sliderPage))
                                                .build();
                        } catch (Exception e) {
                                log.error("💥 Failed to fetch sliders: {}", e.getMessage(), e);
                                return ApiResponsePagination.<List<SliderResponse>>builder()
                                                .status("error")
                                                .message("Failed to fetch sliders")
                                                .data(Collections.emptyList())
                                                .pagination(null)
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponsePagination<List<SliderResponseDeleteAt>> findByActive(FindAllSliderRequest req) {
                Attributes attrs = Attributes.builder()
                                .put("slider.page", String.valueOf(req.getPage()))
                                .put("slider.size", String.valueOf(req.getPageSize()))
                                .build();

                return tracingMetrics.traceAndMeasure("findActiveSliders", "find_active_sliders", attrs, () -> {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                        log.info("🔍 Searching active sliders | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                        keyword.isEmpty() ? "None" : keyword);

                        try {
                                Pageable pageable = PageRequest.of(page, pageSize);
                                Page<Slider> sliderPage = sliderQueryRepository.findActiveSliders(keyword, pageable);

                                List<SliderResponseDeleteAt> responses = sliderPage.getContent()
                                                .stream()
                                                .map(SliderResponseDeleteAt::from)
                                                .toList();

                                log.info("✅ Found {} active sliders", responses.size());

                                return ApiResponsePagination.<List<SliderResponseDeleteAt>>builder()
                                                .status("success")
                                                .message("Active sliders retrieved successfully")
                                                .data(responses)
                                                .pagination(PaginationMeta.fromSpringPage(sliderPage))
                                                .build();
                        } catch (Exception e) {
                                log.error("💥 Failed to fetch active sliders: {}", e.getMessage(), e);
                                return ApiResponsePagination.<List<SliderResponseDeleteAt>>builder()
                                                .status("error")
                                                .message("Failed to fetch active sliders")
                                                .data(Collections.emptyList())
                                                .pagination(null)
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponsePagination<List<SliderResponseDeleteAt>> findByTrashed(FindAllSliderRequest req) {
                Attributes attrs = Attributes.builder()
                                .put("slider.page", String.valueOf(req.getPage()))
                                .put("slider.size", String.valueOf(req.getPageSize()))
                                .build();

                return tracingMetrics.traceAndMeasure("findTrashedSliders", "find_trashed_sliders", attrs, () -> {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                        log.info("🗑 Searching trashed sliders | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                        keyword.isEmpty() ? "None" : keyword);

                        try {
                                Pageable pageable = PageRequest.of(page, pageSize);
                                Page<Slider> sliderPage = sliderQueryRepository.findTrashedSliders(keyword, pageable);

                                List<SliderResponseDeleteAt> responses = sliderPage.getContent()
                                                .stream()
                                                .map(SliderResponseDeleteAt::from)
                                                .toList();

                                log.info("✅ Found {} trashed sliders", responses.size());

                                return ApiResponsePagination.<List<SliderResponseDeleteAt>>builder()
                                                .status("success")
                                                .message("Trashed sliders retrieved successfully")
                                                .data(responses)
                                                .pagination(PaginationMeta.fromSpringPage(sliderPage))
                                                .build();
                        } catch (Exception e) {
                                log.error("💥 Failed to fetch trashed sliders: {}", e.getMessage(), e);
                                return ApiResponsePagination.<List<SliderResponseDeleteAt>>builder()
                                                .status("error")
                                                .message("Failed to fetch trashed sliders")
                                                .data(Collections.emptyList())
                                                .pagination(null)
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponse<SliderResponse> findById(Integer id) {
                Attributes attrs = Attributes.builder()
                                .put("slider.id", String.valueOf(id))
                                .build();

                return tracingMetrics.traceAndMeasure("findSliderById", "find_slider_by_id", attrs, () -> {
                        log.info("🔍 Finding slider by id={}", id);
                        try {
                                return sliderQueryRepository.findById(String.valueOf(id))
                                                .map(slider -> ApiResponse.<SliderResponse>builder()
                                                                .status("success")
                                                                .message("Slider retrieved successfully")
                                                                .data(SliderResponse.from(slider))
                                                                .build())
                                                .orElseGet(() -> {
                                                        log.warn("❌ Slider not found with id={}", id);
                                                        return ApiResponse.<SliderResponse>builder()
                                                                        .status("error")
                                                                        .message("Slider not found")
                                                                        .data(null)
                                                                        .build();
                                                });
                        } catch (Exception e) {
                                log.error("💥 Failed to fetch slider by id={}", id, e);
                                return ApiResponse.<SliderResponse>builder()
                                                .status("error")
                                                .message("Failed to fetch slider")
                                                .data(null)
                                                .build();
                        }
                });
        }
}
