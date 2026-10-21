package com.sanedge.ecommerce.merchant_detail.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.domain.responses.api.PaginationMeta;
import com.sanedge.ecommerce.common.exception.ResourceNotFoundException;
import com.sanedge.ecommerce.common.observability.TracingMetrics;
import com.sanedge.ecommerce.merchant_detail.domain.requests.FindAllMerchantDetailRequest;
import com.sanedge.ecommerce.merchant_detail.domain.responses.MerchantDetailRelationResponse;
import com.sanedge.ecommerce.merchant_detail.domain.responses.MerchantDetailRelationResponseDeleteAt;
import com.sanedge.ecommerce.merchant_detail.models.MerchantDetailsRelation;
import com.sanedge.ecommerce.merchant_detail.repository.MerchantDetailQueryRepository;
import com.sanedge.ecommerce.merchant_detail.service.MerchantDetailQueryService;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantDetailQueryImplService implements MerchantDetailQueryService {

        private final MerchantDetailQueryRepository merchantDetailQueryRepository;
        private final TracingMetrics tracingMetrics;

        @Override
        public ApiResponsePagination<List<MerchantDetailRelationResponse>> findAll(FindAllMerchantDetailRequest req) {
                Attributes attrs = Attributes.builder()
                                .put("detail.page", String.valueOf(req.getPage()))
                                .put("detail.size", String.valueOf(req.getPageSize()))
                                .build();

                return tracingMetrics.traceAndMeasure("findAllMerchantDetails", "find_all_details", attrs, () -> {
                        try {
                                int page = req.getPage() != null && req.getPage() > 0 ? req.getPage() - 1 : 0;
                                int pageSize = req.getPageSize() != null && req.getPageSize() > 0 ? req.getPageSize() : 10;
                                String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                                log.info("🔍 Searching all merchant details | Page: {}, Size: {}, Search: {}",
                                                page + 1, pageSize, keyword.isEmpty() ? "None" : keyword);

                                Pageable pageable = PageRequest.of(page, pageSize);
                                Page<MerchantDetailsRelation> merchantPage = merchantDetailQueryRepository
                                                .findAllWithSocialLinks(keyword, pageable);

                                List<MerchantDetailRelationResponse> responses = merchantPage.getContent()
                                                .stream()
                                                .map(MerchantDetailRelationResponse::from)
                                                .toList();

                                log.info("✅ Found {} merchant details", responses.size());

                                return ApiResponsePagination.<List<MerchantDetailRelationResponse>>builder()
                                                .status("success")
                                                .message("Merchant details retrieved successfully")
                                                .data(responses)
                                                .pagination(PaginationMeta.fromSpringPage(merchantPage))
                                                .build();

                        } catch (Exception e) {
                                log.error("❌ Failed to retrieve merchant details: {}", e.getMessage(), e);
                                return ApiResponsePagination.<List<MerchantDetailRelationResponse>>builder()
                                                .status("error")
                                                .message("Failed to retrieve merchant details")
                                                .data(List.of())
                                                .pagination(null)
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponsePagination<List<MerchantDetailRelationResponseDeleteAt>> findByActive(
                        FindAllMerchantDetailRequest req) {
                Attributes attrs = Attributes.builder()
                                .put("detail.page", String.valueOf(req.getPage()))
                                .put("detail.size", String.valueOf(req.getPageSize()))
                                .build();

                return tracingMetrics.traceAndMeasure("findActiveMerchantDetails", "find_active_details", attrs, () -> {
                        try {
                                int page = req.getPage() != null && req.getPage() > 0 ? req.getPage() - 1 : 0;
                                int pageSize = req.getPageSize() != null && req.getPageSize() > 0 ? req.getPageSize() : 10;
                                String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                                log.info("🔍 Searching active merchant details | Page: {}, Size: {}", page + 1, pageSize);

                                Pageable pageable = PageRequest.of(page, pageSize);
                                Page<MerchantDetailsRelation> merchantPage = merchantDetailQueryRepository
                                                .findActiveWithSocialLinks(keyword, pageable);

                                List<MerchantDetailRelationResponseDeleteAt> responses = merchantPage.getContent()
                                                .stream()
                                                .map(MerchantDetailRelationResponseDeleteAt::from)
                                                .toList();

                                log.info("✅ Found {} active merchant details", responses.size());

                                return ApiResponsePagination.<List<MerchantDetailRelationResponseDeleteAt>>builder()
                                                .status("success")
                                                .message("Active merchant details retrieved successfully")
                                                .data(responses)
                                                .pagination(PaginationMeta.fromSpringPage(merchantPage))
                                                .build();

                        } catch (Exception e) {
                                log.error("❌ Failed to retrieve active merchant details: {}", e.getMessage(), e);
                                return ApiResponsePagination.<List<MerchantDetailRelationResponseDeleteAt>>builder()
                                                .status("error")
                                                .message("Failed to retrieve active merchant details")
                                                .data(List.of())
                                                .pagination(null)
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponsePagination<List<MerchantDetailRelationResponseDeleteAt>> findByTrashed(
                        FindAllMerchantDetailRequest req) {
                Attributes attrs = Attributes.builder()
                                .put("detail.page", String.valueOf(req.getPage()))
                                .put("detail.size", String.valueOf(req.getPageSize()))
                                .build();

                return tracingMetrics.traceAndMeasure("findTrashedMerchantDetails", "find_trashed_details", attrs, () -> {
                        try {
                                int page = req.getPage() != null && req.getPage() > 0 ? req.getPage() - 1 : 0;
                                int pageSize = req.getPageSize() != null && req.getPageSize() > 0 ? req.getPageSize() : 10;
                                String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                                log.info("🗑️ Searching trashed merchant details | Page: {}, Size: {}", page + 1, pageSize);

                                Pageable pageable = PageRequest.of(page, pageSize);
                                Page<MerchantDetailsRelation> merchantPage = merchantDetailQueryRepository
                                                .findTrashedWithSocialLinks(keyword, pageable);

                                List<MerchantDetailRelationResponseDeleteAt> responses = merchantPage.getContent()
                                                .stream()
                                                .map(MerchantDetailRelationResponseDeleteAt::from)
                                                .toList();

                                log.info("✅ Found {} trashed merchant details", responses.size());

                                return ApiResponsePagination.<List<MerchantDetailRelationResponseDeleteAt>>builder()
                                                .status("success")
                                                .message("Trashed merchant details retrieved successfully")
                                                .data(responses)
                                                .pagination(PaginationMeta.fromSpringPage(merchantPage))
                                                .build();

                        } catch (Exception e) {
                                log.error("❌ Failed to retrieve trashed merchant details: {}", e.getMessage(), e);
                                return ApiResponsePagination.<List<MerchantDetailRelationResponseDeleteAt>>builder()
                                                .status("error")
                                                .message("Failed to retrieve trashed merchant details")
                                                .data(List.of())
                                                .pagination(null)
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponse<MerchantDetailRelationResponse> findById(Integer merchantID) {
                Attributes attrs = Attributes.builder()
                                .put("merchant.id", String.valueOf(merchantID))
                                .build();

                return tracingMetrics.traceAndMeasure("findMerchantDetailById", "find_detail_by_id", attrs, () -> {
                        try {
                                log.info("🔍 Searching merchant detail by ID: {}", merchantID);

                                MerchantDetailsRelation entity = merchantDetailQueryRepository
                                                .findByIdWithSocialLinks(String.valueOf(merchantID))
                                                .orElseThrow(() -> new ResourceNotFoundException(
                                                                "Merchant detail not found with ID: " + merchantID));

                                log.info("✅ Found merchant detail with ID: {}", merchantID);

                                return ApiResponse.<MerchantDetailRelationResponse>builder()
                                                .status("success")
                                                .message("Merchant Detail retrieved successfully")
                                                .data(MerchantDetailRelationResponse.from(entity))
                                                .build();

                        } catch (ResourceNotFoundException ex) {
                                log.warn("❌ {}", ex.getMessage());
                                return ApiResponse.<MerchantDetailRelationResponse>builder()
                                                .status("error")
                                                .message(ex.getMessage())
                                                .data(null)
                                                .build();
                        } catch (Exception ex) {
                                log.error("❌ Failed to retrieve merchant detail by ID {}: {}", merchantID, ex.getMessage(), ex);
                                return ApiResponse.<MerchantDetailRelationResponse>builder()
                                                .status("error")
                                                .message("Failed to retrieve merchant detail")
                                                .data(null)
                                                .build();
                        }
                });
        }
}
