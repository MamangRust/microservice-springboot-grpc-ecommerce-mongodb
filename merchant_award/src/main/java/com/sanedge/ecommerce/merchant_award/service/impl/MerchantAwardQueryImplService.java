package com.sanedge.ecommerce.merchant_award.service.impl;

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
import com.sanedge.ecommerce.merchant_award.domain.requests.FindAllMerchantAwards;
import com.sanedge.ecommerce.merchant_award.domain.responses.MerchantAwardResponse;
import com.sanedge.ecommerce.merchant_award.domain.responses.MerchantAwardResponseDeleteAt;
import com.sanedge.ecommerce.merchant_award.models.MerchantCertificationAndAward;
import com.sanedge.ecommerce.merchant_award.repository.MerchantAwardQueryRepository;
import com.sanedge.ecommerce.merchant_award.service.MerchantAwardQueryService;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantAwardQueryImplService implements MerchantAwardQueryService {

        private final MerchantAwardQueryRepository merchantAwardQueryRepository;
        private final TracingMetrics tracingMetrics;

        @Override
        public ApiResponsePagination<List<MerchantAwardResponse>> findAll(FindAllMerchantAwards req) {
                Attributes attrs = Attributes.builder()
                                .put("award.page", String.valueOf(req.getPage()))
                                .put("award.size", String.valueOf(req.getPageSize()))
                                .build();

                return tracingMetrics.traceAndMeasure("findAllMerchantAwards", "find_all_awards", attrs, () -> {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                        log.info("🔍 Searching merchant awards | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                        keyword.isEmpty() ? "None" : keyword);

                        try {
                                Pageable pageable = PageRequest.of(page, pageSize);
                                Page<MerchantCertificationAndAward> awardPage = merchantAwardQueryRepository
                                                .findMerchantAwards(keyword, pageable);

                                List<MerchantAwardResponse> responses = awardPage.getContent()
                                                .stream()
                                                .map(MerchantAwardResponse::from)
                                                .toList();

                                return ApiResponsePagination.<List<MerchantAwardResponse>>builder()
                                                .status("success")
                                                .message("Merchant awards retrieved successfully")
                                                .data(responses)
                                                .pagination(PaginationMeta.fromSpringPage(awardPage))
                                                .build();

                        } catch (Exception e) {
                                log.error("❌ Failed to fetch merchant awards", e);
                                return ApiResponsePagination.<List<MerchantAwardResponse>>builder()
                                                .status("error")
                                                .message("Failed to fetch merchant awards")
                                                .data(Collections.emptyList())
                                                .pagination(null)
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponsePagination<List<MerchantAwardResponseDeleteAt>> findByActive(FindAllMerchantAwards req) {
                Attributes attrs = Attributes.builder()
                                .put("award.page", String.valueOf(req.getPage()))
                                .put("award.size", String.valueOf(req.getPageSize()))
                                .build();

                return tracingMetrics.traceAndMeasure("findActiveMerchantAwards", "find_active_awards", attrs, () -> {
                        log.info("🔍 Searching ACTIVE merchant awards");
                        try {
                                Pageable pageable = PageRequest.of(req.getPage() - 1, req.getPageSize());
                                Page<MerchantCertificationAndAward> awardPage = merchantAwardQueryRepository
                                                .findActiveMerchantAwards(req.getSearch(), pageable);

                                List<MerchantAwardResponseDeleteAt> responses = awardPage.getContent()
                                                .stream()
                                                .map(MerchantAwardResponseDeleteAt::from)
                                                .toList();

                                return ApiResponsePagination.<List<MerchantAwardResponseDeleteAt>>builder()
                                                .status("success")
                                                .message("Active merchant awards retrieved successfully")
                                                .data(responses)
                                                .pagination(PaginationMeta.fromSpringPage(awardPage))
                                                .build();

                        } catch (Exception e) {
                                log.error("❌ Failed to fetch active merchant awards", e);
                                return ApiResponsePagination.<List<MerchantAwardResponseDeleteAt>>builder()
                                                .status("error")
                                                .message("Failed to fetch active merchant awards")
                                                .data(Collections.emptyList())
                                                .pagination(null)
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponsePagination<List<MerchantAwardResponseDeleteAt>> findByTrashed(FindAllMerchantAwards req) {
                Attributes attrs = Attributes.builder()
                                .put("award.page", String.valueOf(req.getPage()))
                                .put("award.size", String.valueOf(req.getPageSize()))
                                .build();

                return tracingMetrics.traceAndMeasure("findTrashedMerchantAwards", "find_trashed_awards", attrs, () -> {
                        log.info("🔍 Searching TRASHED merchant awards");
                        try {
                                Pageable pageable = PageRequest.of(req.getPage() - 1, req.getPageSize());
                                Page<MerchantCertificationAndAward> awardPage = merchantAwardQueryRepository
                                                .findTrashedMerchantAwards(req.getSearch(), pageable);

                                List<MerchantAwardResponseDeleteAt> responses = awardPage.getContent()
                                                .stream()
                                                .map(MerchantAwardResponseDeleteAt::from)
                                                .toList();

                                return ApiResponsePagination.<List<MerchantAwardResponseDeleteAt>>builder()
                                                .status("success")
                                                .message("Trashed merchant awards retrieved successfully")
                                                .data(responses)
                                                .pagination(PaginationMeta.fromSpringPage(awardPage))
                                                .build();

                        } catch (Exception e) {
                                log.error("❌ Failed to fetch trashed merchant awards", e);
                                return ApiResponsePagination.<List<MerchantAwardResponseDeleteAt>>builder()
                                                .status("error")
                                                .message("Failed to fetch trashed merchant awards")
                                                .data(Collections.emptyList())
                                                .pagination(null)
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponse<MerchantAwardResponse> findById(Integer merchantAwardId) {
                Attributes attrs = Attributes.builder()
                                .put("award.id", String.valueOf(merchantAwardId))
                                .build();

                return tracingMetrics.traceAndMeasure("findMerchantAwardById", "find_award_by_id", attrs, () -> {
                        log.info("🔍 Finding merchant award by ID: {}", merchantAwardId);
                        try {
                                return merchantAwardQueryRepository.findById(String.valueOf(merchantAwardId))
                                                .map(award -> ApiResponse.<MerchantAwardResponse>builder()
                                                                .status("success")
                                                                .message("Merchant award retrieved successfully")
                                                                .data(MerchantAwardResponse.from(award))
                                                                .build())
                                                .orElseGet(() -> ApiResponse.<MerchantAwardResponse>builder()
                                                                .status("error")
                                                                .message("Merchant award not found with id " + merchantAwardId)
                                                                .data(null)
                                                                .build());

                        } catch (Exception e) {
                                log.error("❌ Failed to fetch merchant award by ID", e);
                                return ApiResponse.<MerchantAwardResponse>builder()
                                                .status("error")
                                                .message("Failed to fetch merchant award")
                                                .data(null)
                                                .build();
                        }
                });
        }
}
