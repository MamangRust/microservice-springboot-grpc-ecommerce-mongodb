package com.sanedge.ecommerce.merchant_business.service.impl;

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
import com.sanedge.ecommerce.merchant_business.domain.requests.FindAllMerchantRequest;
import com.sanedge.ecommerce.merchant_business.domain.responses.MerchantBusinessResponse;
import com.sanedge.ecommerce.merchant_business.domain.responses.MerchantBusinessResponseDeleteAt;
import com.sanedge.ecommerce.merchant_business.models.MerchantBusinessInformation;
import com.sanedge.ecommerce.merchant_business.repository.MerchantBusinessQueryRepository;
import com.sanedge.ecommerce.merchant_business.service.MerchantBusinessQueryService;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantBusinessQueryImplService implements MerchantBusinessQueryService {

        private final MerchantBusinessQueryRepository merchantBusinessQueryRepository;
        private final TracingMetrics tracingMetrics;

        @Override
        public ApiResponsePagination<List<MerchantBusinessResponse>> findAll(FindAllMerchantRequest req) {
                Attributes attrs = Attributes.builder()
                                .put("business.page", String.valueOf(req.getPage()))
                                .put("business.size", String.valueOf(req.getPageSize()))
                                .build();

                return tracingMetrics.traceAndMeasure("findAllMerchantBusiness", "find_all_business_info", attrs, () -> {
                        log.info("🔍 Finding all merchant business info with search: {}", req.getSearch());
                        try {
                                Pageable pageable = PageRequest.of(pageIndex(req), pageSize(req));
                                Page<MerchantBusinessInformation> page = merchantBusinessQueryRepository
                                                .findMerchantBusinessInformation(keyword(req), pageable);

                                List<MerchantBusinessResponse> business = page.getContent()
                                                .stream()
                                                .map(MerchantBusinessResponse::from)
                                                .toList();

                                return ApiResponsePagination.<List<MerchantBusinessResponse>>builder()
                                                .status("success")
                                                .message("Merchant business information retrieved successfully")
                                                .data(business)
                                                .pagination(PaginationMeta.fromSpringPage(page))
                                                .build();

                        } catch (Exception e) {
                                log.error("❌ Failed to fetch merchant business information", e);
                                return ApiResponsePagination.<List<MerchantBusinessResponse>>builder()
                                                .status("error")
                                                .message("Failed to fetch merchant business information")
                                                .data(Collections.emptyList())
                                                .pagination(null)
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponsePagination<List<MerchantBusinessResponseDeleteAt>> findByActive(FindAllMerchantRequest req) {
                Attributes attrs = Attributes.builder()
                                .put("business.page", String.valueOf(req.getPage()))
                                .put("business.size", String.valueOf(req.getPageSize()))
                                .build();

                return tracingMetrics.traceAndMeasure("findActiveMerchantBusiness", "find_active_business_info", attrs,
                                () -> {
                                        log.info("🔍 Finding all ACTIVE merchant business info");
                                        try {
                                                Pageable pageable = PageRequest.of(pageIndex(req), pageSize(req));
                                                Page<MerchantBusinessInformation> page = merchantBusinessQueryRepository
                                                                .findActiveMerchantBusinessInformation(keyword(req), pageable);

                                                List<MerchantBusinessResponseDeleteAt> business = page.getContent()
                                                                .stream()
                                                                .map(MerchantBusinessResponseDeleteAt::from)
                                                                .toList();

                                                return ApiResponsePagination
                                                                .<List<MerchantBusinessResponseDeleteAt>>builder()
                                                                .status("success")
                                                                .message("Active merchant business information retrieved successfully")
                                                                .data(business)
                                                                .pagination(PaginationMeta.fromSpringPage(page))
                                                                .build();

                                        } catch (Exception e) {
                                                log.error("❌ Failed to fetch ACTIVE merchant business information", e);
                                                return ApiResponsePagination
                                                                .<List<MerchantBusinessResponseDeleteAt>>builder()
                                                                .status("error")
                                                                .message("Failed to fetch active merchant business information")
                                                                .data(Collections.emptyList())
                                                                .pagination(null)
                                                                .build();
                                        }
                                });
        }

        @Override
        public ApiResponsePagination<List<MerchantBusinessResponseDeleteAt>> findByTrashed(FindAllMerchantRequest req) {
                Attributes attrs = Attributes.builder()
                                .put("business.page", String.valueOf(req.getPage()))
                                .put("business.size", String.valueOf(req.getPageSize()))
                                .build();

                return tracingMetrics.traceAndMeasure("findTrashedMerchantBusiness", "find_trashed_business_info", attrs,
                                () -> {
                                        log.info("🔍 Finding all TRASHED merchant business info");
                                        try {
                                                Pageable pageable = PageRequest.of(pageIndex(req), pageSize(req));
                                                Page<MerchantBusinessInformation> page = merchantBusinessQueryRepository
                                                                .findTrashedMerchantBusinessInformation(keyword(req),
                                                                                pageable);

                                                List<MerchantBusinessResponseDeleteAt> business = page.getContent()
                                                                .stream()
                                                                .map(MerchantBusinessResponseDeleteAt::from)
                                                                .toList();

                                                return ApiResponsePagination
                                                                .<List<MerchantBusinessResponseDeleteAt>>builder()
                                                                .status("success")
                                                                .message("Trashed merchant business information retrieved successfully")
                                                                .data(business)
                                                                .pagination(PaginationMeta.fromSpringPage(page))
                                                                .build();

                                        } catch (Exception e) {
                                                log.error("❌ Failed to fetch TRASHED merchant business information", e);
                                                return ApiResponsePagination
                                                                .<List<MerchantBusinessResponseDeleteAt>>builder()
                                                                .status("error")
                                                                .message("Failed to fetch trashed merchant business information")
                                                                .data(Collections.emptyList())
                                                                .pagination(null)
                                                                .build();
                                        }
                                });
        }

        @Override
        public ApiResponse<MerchantBusinessResponse> findById(Integer merchantBusinessInfoId) {
                Attributes attrs = Attributes.builder()
                                .put("business.id", String.valueOf(merchantBusinessInfoId))
                                .build();

                return tracingMetrics.traceAndMeasure("findMerchantBusinessById", "find_business_info_by_id", attrs,
                                () -> {
                                        try {
                                                log.info("🔍 Finding merchant business info by ID: {}", merchantBusinessInfoId);

                                                return merchantBusinessQueryRepository
                                                                .findById(String.valueOf(merchantBusinessInfoId))
                                                                .map(business -> ApiResponse
                                                                                .<MerchantBusinessResponse>builder()
                                                                                .status("success")
                                                                                .message("Merchant business info retrieved successfully")
                                                                                .data(MerchantBusinessResponse.from(business))
                                                                                .build())
                                                                .orElseGet(() -> ApiResponse
                                                                                .<MerchantBusinessResponse>builder()
                                                                                .status("error")
                                                                                .message("Merchant business info not found with id "
                                                                                                + merchantBusinessInfoId)
                                                                                .data(null)
                                                                                .build());

                                        } catch (Exception ex) {
                                                log.error("❌ Failed to retrieve merchant business info: {}",
                                                                ex.getMessage(), ex);
                                                return ApiResponse.<MerchantBusinessResponse>builder()
                                                                .status("error")
                                                                .message("Failed to retrieve merchant business info")
                                                                .data(null)
                                                                .build();
                                        }
                                });
        }

        private int pageIndex(FindAllMerchantRequest req) {
                return req.getPage() != null && req.getPage() > 0 ? req.getPage() - 1 : 0;
        }

        private int pageSize(FindAllMerchantRequest req) {
                return req.getPageSize() != null && req.getPageSize() > 0 ? req.getPageSize() : 10;
        }

        private String keyword(FindAllMerchantRequest req) {
                return req.getSearch() != null && !req.getSearch().isEmpty() ? req.getSearch() : "";
        }
}
