package com.sanedge.ecommerce.review.service.impl;

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
import com.sanedge.ecommerce.review.domain.requests.FindAllReview;
import com.sanedge.ecommerce.review.domain.requests.FindAllReviewByMerchant;
import com.sanedge.ecommerce.review.domain.requests.FindAllReviewByProduct;
import com.sanedge.ecommerce.review.domain.responses.ReviewRelationsDetailResponse;
import com.sanedge.ecommerce.review.domain.responses.ReviewResponse;
import com.sanedge.ecommerce.review.domain.responses.ReviewResponseDeleteAt;
import com.sanedge.ecommerce.review.models.Review;
import com.sanedge.ecommerce.review.models.ReviewRelationsDetail;
import com.sanedge.ecommerce.review.repository.ReviewQueryRepository;
import com.sanedge.ecommerce.review.service.ReviewQueryService;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewQueryImplService implements ReviewQueryService {

        private final ReviewQueryRepository reviewQueryRepository;
        private final TracingMetrics tracingMetrics;

        @Override
        public ApiResponsePagination<List<ReviewResponse>> findAll(FindAllReview req) {
                return tracingMetrics.traceAndMeasure("findAllReviews", "find_all_reviews", () -> {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                        log.info("🔍 Searching all reviews | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                        keyword.isEmpty() ? "None" : keyword);

                        try {
                                Pageable pageable = PageRequest.of(page, pageSize);
                                Page<Review> reviewPage = reviewQueryRepository.findReviews(keyword, pageable);

                                List<ReviewResponse> responses = reviewPage.getContent()
                                                .stream()
                                                .map(ReviewResponse::from)
                                                .toList();

                                log.info("✅ Found {} reviews", responses.size());

                                return ApiResponsePagination.<List<ReviewResponse>>builder()
                                                .status("success")
                                                .message("Reviews retrieved successfully")
                                                .data(responses)
                                                .pagination(PaginationMeta.fromSpringPage(reviewPage))
                                                .build();
                        } catch (Exception e) {
                                log.error("💥 Failed to fetch reviews", e);
                                return ApiResponsePagination.<List<ReviewResponse>>builder()
                                                .status("error")
                                                .message("Failed to fetch reviews")
                                                .data(Collections.emptyList())
                                                .pagination(null)
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponsePagination<List<ReviewResponseDeleteAt>> findActive(FindAllReview req) {
                return tracingMetrics.traceAndMeasure("findActiveReviews", "find_active_reviews", () -> {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                        log.info("🔍 Searching active reviews | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                        keyword.isEmpty() ? "None" : keyword);

                        try {
                                Pageable pageable = PageRequest.of(page, pageSize);
                                Page<Review> reviewPage = reviewQueryRepository.findActiveReviews(keyword, pageable);

                                List<ReviewResponseDeleteAt> responses = reviewPage.getContent()
                                                .stream()
                                                .map(ReviewResponseDeleteAt::from)
                                                .toList();

                                log.info("✅ Found {} active reviews", responses.size());

                                return ApiResponsePagination.<List<ReviewResponseDeleteAt>>builder()
                                                .status("success")
                                                .message("Active reviews retrieved successfully")
                                                .data(responses)
                                                .pagination(PaginationMeta.fromSpringPage(reviewPage))
                                                .build();
                        } catch (Exception e) {
                                log.error("💥 Failed to fetch active reviews", e);
                                return ApiResponsePagination.<List<ReviewResponseDeleteAt>>builder()
                                                .status("error")
                                                .message("Failed to fetch active reviews")
                                                .data(Collections.emptyList())
                                                .pagination(null)
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponsePagination<List<ReviewResponseDeleteAt>> findTrashed(FindAllReview req) {
                return tracingMetrics.traceAndMeasure("findTrashedReviews", "find_trashed_reviews", () -> {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                        log.info("🔍 Searching trashed reviews | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                        keyword.isEmpty() ? "None" : keyword);

                        try {
                                Pageable pageable = PageRequest.of(page, pageSize);
                                Page<Review> reviewPage = reviewQueryRepository.findTrashedReviews(keyword, pageable);

                                List<ReviewResponseDeleteAt> responses = reviewPage.getContent()
                                                .stream()
                                                .map(ReviewResponseDeleteAt::from)
                                                .toList();

                                log.info("✅ Found {} trashed reviews", responses.size());

                                return ApiResponsePagination.<List<ReviewResponseDeleteAt>>builder()
                                                .status("success")
                                                .message("Trashed reviews retrieved successfully")
                                                .data(responses)
                                                .pagination(PaginationMeta.fromSpringPage(reviewPage))
                                                .build();
                        } catch (Exception e) {
                                log.error("💥 Failed to fetch trashed reviews", e);
                                return ApiResponsePagination.<List<ReviewResponseDeleteAt>>builder()
                                                .status("error")
                                                .message("Failed to fetch trashed reviews")
                                                .data(Collections.emptyList())
                                                .pagination(null)
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponsePagination<List<ReviewRelationsDetailResponse>> findByMerchant(FindAllReviewByMerchant req) {
                Attributes attrs = Attributes.builder()
                                .put("merchant.id", String.valueOf(req.getMerchantId()))
                                .build();

                return tracingMetrics.traceAndMeasure("findReviewsByMerchant", "find_reviews_by_merchant", attrs, () -> {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                        log.info("🏪 Searching reviews by merchantId={} | rating={}, Page={}, Size={}",
                                        req.getMerchantId(), req.getRating(), page + 1, pageSize);

                        try {
                                Pageable pageable = PageRequest.of(page, pageSize);
                                Page<ReviewRelationsDetail> reviewPage = reviewQueryRepository.findByMerchantId(
                                                req.getMerchantId(), req.getRating(), keyword, pageable);

                                List<ReviewRelationsDetailResponse> responses = reviewPage.getContent()
                                                .stream()
                                                .map(ReviewRelationsDetailResponse::from)
                                                .toList();

                                log.info("✅ Found {} reviews for merchantId={}", responses.size(), req.getMerchantId());

                                return ApiResponsePagination.<List<ReviewRelationsDetailResponse>>builder()
                                                .status("success")
                                                .message("Merchant reviews retrieved successfully")
                                                .data(responses)
                                                .pagination(PaginationMeta.fromSpringPage(reviewPage))
                                                .build();
                        } catch (Exception e) {
                                log.error("💥 Failed to fetch reviews by merchantId={}", req.getMerchantId(), e);
                                return ApiResponsePagination.<List<ReviewRelationsDetailResponse>>builder()
                                                .status("error")
                                                .message("Failed to fetch reviews by merchant")
                                                .data(Collections.emptyList())
                                                .pagination(null)
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponsePagination<List<ReviewRelationsDetailResponse>> findByProduct(FindAllReviewByProduct req) {
                Attributes attrs = Attributes.builder()
                                .put("product.id", String.valueOf(req.getProductId()))
                                .build();

                return tracingMetrics.traceAndMeasure("findReviewsByProduct", "find_reviews_by_product", attrs, () -> {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                        log.info("📦 Searching reviews by productId={} | rating={}, Page={}, Size={}",
                                        req.getProductId(), req.getRating(), page + 1, pageSize);

                        try {
                                Pageable pageable = PageRequest.of(page, pageSize);
                                Page<ReviewRelationsDetail> reviewPage = reviewQueryRepository.findByProductId(
                                                req.getProductId(), req.getRating(), keyword, pageable);

                                List<ReviewRelationsDetailResponse> responses = reviewPage.getContent()
                                                .stream()
                                                .map(ReviewRelationsDetailResponse::from)
                                                .toList();

                                log.info("✅ Found {} reviews for productId={}", responses.size(), req.getProductId());

                                return ApiResponsePagination.<List<ReviewRelationsDetailResponse>>builder()
                                                .status("success")
                                                .message("Product reviews retrieved successfully")
                                                .data(responses)
                                                .pagination(PaginationMeta.fromSpringPage(reviewPage))
                                                .build();
                        } catch (Exception e) {
                                log.error("💥 Failed to fetch reviews by productId={}", req.getProductId(), e);
                                return ApiResponsePagination.<List<ReviewRelationsDetailResponse>>builder()
                                                .status("error")
                                                .message("Failed to fetch reviews by product")
                                                .data(Collections.emptyList())
                                                .pagination(null)
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponse<ReviewResponse> findById(Integer reviewId) {
                Attributes attrs = Attributes.builder()
                                .put("review.id", String.valueOf(reviewId))
                                .build();

                return tracingMetrics.traceAndMeasure("findReviewById", "find_review_by_id", attrs, () -> {
                        log.info("🔍 Finding review by id={}", reviewId);
                        try {
                                return reviewQueryRepository.findById(String.valueOf(reviewId))
                                                .map(review -> ApiResponse.<ReviewResponse>builder()
                                                                .status("success")
                                                                .message("Review retrieved successfully")
                                                                .data(ReviewResponse.from(review))
                                                                .build())
                                                .orElseGet(() -> {
                                                        log.warn("❌ Review not found with id={}", reviewId);
                                                        return ApiResponse.<ReviewResponse>builder()
                                                                        .status("error")
                                                                        .message("Review not found")
                                                                        .data(null)
                                                                        .build();
                                                });
                        } catch (Exception e) {
                                log.error("💥 Failed to fetch review by id={}", reviewId, e);
                                return ApiResponse.<ReviewResponse>builder()
                                                .status("error")
                                                .message("Failed to fetch review")
                                                .data(null)
                                                .build();
                        }
                });
        }
}
