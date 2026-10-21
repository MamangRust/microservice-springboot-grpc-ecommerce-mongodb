package com.sanedge.ecommerce.merchant_policy.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.domain.responses.api.PaginationMeta;
import com.sanedge.ecommerce.common.observability.TracingMetrics;
import com.sanedge.ecommerce.merchant_policy.domain.requests.FindAllMerchantPolicies;
import com.sanedge.ecommerce.merchant_policy.domain.responses.MerchantPoliciesResponse;
import com.sanedge.ecommerce.merchant_policy.domain.responses.MerchantPoliciesResponseDeleteAt;
import com.sanedge.ecommerce.merchant_policy.models.MerchantPolicy;
import com.sanedge.ecommerce.merchant_policy.repository.MerchantPolicyQueryRepository;
import com.sanedge.ecommerce.merchant_policy.service.MerchantPolicyQueryService;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantPolicyQueryImplService implements MerchantPolicyQueryService {
        private final MerchantPolicyQueryRepository merchantPolicyQueryRepository;
        private final TracingMetrics tracingMetrics;

        @Override
        public ApiResponsePagination<List<MerchantPoliciesResponse>> findAll(FindAllMerchantPolicies req) {
                return tracingMetrics.traceAndMeasure("findAllMerchantPolicies", "find_all_policies", () -> {
                        try {
                                int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                                int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                                String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                                log.info("🔍 Searching all merchant policies | Page: {}, Size: {}, Search: {}", page + 1,
                                                pageSize,
                                                keyword.isEmpty() ? "None" : keyword);

                                Pageable pageable = PageRequest.of(page, pageSize);
                                Page<MerchantPolicy> policyPage = merchantPolicyQueryRepository.findMerchantPolicies(keyword,
                                                pageable);

                                List<MerchantPoliciesResponse> responses = policyPage.getContent()
                                                .stream()
                                                .map(MerchantPoliciesResponse::from)
                                                .toList();

                                log.info("✅ Found {} merchant policies", responses.size());

                                return ApiResponsePagination.<List<MerchantPoliciesResponse>>builder()
                                                .status("success")
                                                .message("Merchant policies retrieved successfully")
                                                .data(responses)
                                                .pagination(PaginationMeta.fromSpringPage(policyPage))
                                                .build();

                        } catch (Exception e) {
                                log.error("❌ Error while fetching merchant policies", e);
                                return ApiResponsePagination.<List<MerchantPoliciesResponse>>builder()
                                                .status("error")
                                                .message("Failed to fetch merchant policies")
                                                .data(List.of())
                                                .pagination(null)
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponsePagination<List<MerchantPoliciesResponseDeleteAt>> findByActive(FindAllMerchantPolicies req) {
                return tracingMetrics.traceAndMeasure("findActiveMerchantPolicies", "find_active_policies", () -> {
                        try {
                                int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                                int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                                String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                                log.info("🔍 Searching active merchant policies | Page: {}, Size: {}, Search: {}", page + 1,
                                                pageSize,
                                                keyword.isEmpty() ? "None" : keyword);

                                Pageable pageable = PageRequest.of(page, pageSize);
                                Page<MerchantPolicy> policyPage = merchantPolicyQueryRepository.findActiveMerchantPolicies(
                                                keyword,
                                                pageable);

                                List<MerchantPoliciesResponseDeleteAt> responses = policyPage.getContent()
                                                .stream()
                                                .map(MerchantPoliciesResponseDeleteAt::from)
                                                .toList();

                                log.info("✅ Found {} active merchant policies", responses.size());

                                return ApiResponsePagination.<List<MerchantPoliciesResponseDeleteAt>>builder()
                                                .status("success")
                                                .message("Active merchant policies retrieved successfully")
                                                .data(responses)
                                                .pagination(PaginationMeta.fromSpringPage(policyPage))
                                                .build();

                        } catch (Exception e) {
                                log.error("❌ Error while fetching active merchant policies", e);
                                return ApiResponsePagination.<List<MerchantPoliciesResponseDeleteAt>>builder()
                                                .status("error")
                                                .message("Failed to fetch active merchant policies")
                                                .data(List.of())
                                                .pagination(null)
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponsePagination<List<MerchantPoliciesResponseDeleteAt>> findByTrashed(FindAllMerchantPolicies req) {
                return tracingMetrics.traceAndMeasure("findTrashedMerchantPolicies", "find_trashed_policies", () -> {
                        try {
                                int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                                int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                                String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                                log.info("🔍 Searching trashed merchant policies | Page: {}, Size: {}, Search: {}", page + 1,
                                                pageSize,
                                                keyword.isEmpty() ? "None" : keyword);

                                Pageable pageable = PageRequest.of(page, pageSize);
                                Page<MerchantPolicy> policyPage = merchantPolicyQueryRepository.findTrashedMerchantPolicies(
                                                keyword,
                                                pageable);

                                List<MerchantPoliciesResponseDeleteAt> responses = policyPage.getContent()
                                                .stream()
                                                .map(MerchantPoliciesResponseDeleteAt::from)
                                                .toList();

                                log.info("✅ Found {} trashed merchant policies", responses.size());

                                return ApiResponsePagination.<List<MerchantPoliciesResponseDeleteAt>>builder()
                                                .status("success")
                                                .message("Trashed merchant policies retrieved successfully")
                                                .data(responses)
                                                .pagination(PaginationMeta.fromSpringPage(policyPage))
                                                .build();

                        } catch (Exception e) {
                                log.error("❌ Error while fetching trashed merchant policies", e);
                                return ApiResponsePagination.<List<MerchantPoliciesResponseDeleteAt>>builder()
                                                .status("error")
                                                .message("Failed to fetch trashed merchant policies")
                                                .data(List.of())
                                                .pagination(null)
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponse<MerchantPoliciesResponse> findById(Integer id) {
                Attributes attrs = Attributes.builder()
                                .put("policy.id", String.valueOf(id))
                                .build();

                return tracingMetrics.traceAndMeasure("findMerchantPolicyById", "find_policy_by_id", attrs, () -> {
                        try {
                                log.info("🔍 Finding merchant policy by id={}", id);

                                Optional<MerchantPolicy> optionalPolicy = merchantPolicyQueryRepository
                                                .findById(String.valueOf(id));
                                if (optionalPolicy.isEmpty()) {
                                        return ApiResponse.<MerchantPoliciesResponse>builder()
                                                        .status("error")
                                                        .message("Merchant policy not found with id=" + id)
                                                        .data(null)
                                                        .build();
                                }

                                return ApiResponse.<MerchantPoliciesResponse>builder()
                                                .status("success")
                                                .message("Merchant policy retrieved successfully")
                                                .data(MerchantPoliciesResponse.from(optionalPolicy.get()))
                                                .build();

                        } catch (Exception e) {
                                log.error("❌ Error while fetching merchant policy by id={}", id, e);
                                return ApiResponse.<MerchantPoliciesResponse>builder()
                                                .status("error")
                                                .message("Failed to fetch merchant policy")
                                                .data(null)
                                                .build();
                        }
                });
        }
}
