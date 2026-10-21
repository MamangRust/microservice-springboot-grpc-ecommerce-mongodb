package com.sanedge.ecommerce.review_detail.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.domain.responses.api.PaginationMeta;
import com.sanedge.ecommerce.common.observability.TracingMetrics;
import com.sanedge.ecommerce.review_detail.domain.requests.reviewdetail.CreateReviewDetailRequest;
import com.sanedge.ecommerce.review_detail.domain.requests.reviewdetail.UpdateReviewDetailRequest;
import com.sanedge.ecommerce.review_detail.domain.responses.reviewdetail.ReviewDetailResponse;
import com.sanedge.ecommerce.review_detail.domain.responses.reviewdetail.ReviewDetailResponseDeleteAt;
import com.sanedge.ecommerce.review_detail.models.review.ReviewDetail;
import com.sanedge.ecommerce.review_detail.repository.review_detail.ReviewDetailRepository;
import com.sanedge.ecommerce.review_detail.service.reviewdetail.ReviewDetailService;

import io.opentelemetry.api.common.Attributes;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewDetailImplService implements ReviewDetailService {
    private final ReviewDetailRepository reviewDetailRepository;
    private final Validator validator;
    private final TracingMetrics tracingMetrics;

    @Override
    public ApiResponse<List<ReviewDetailResponse>> create(List<CreateReviewDetailRequest> requests) {
        return tracingMetrics.traceAndMeasure("createReviewDetails", "create_review_details", () -> {
            List<ReviewDetailResponse> responses = new ArrayList<>();
            try {
                validateRequest(requests);

                for (CreateReviewDetailRequest req : requests) {
                    log.info("🆕 Creating review detail for reviewId={} type={}", req.getReviewId(), req.getType());

                    ReviewDetail reviewDetail = new ReviewDetail();
                    reviewDetail.setReviewId(req.getReviewId());
                    reviewDetail.setType(req.getType());
                    reviewDetail.setUrl(req.getFile());
                    reviewDetail.setCaption(req.getCaption());
                    reviewDetail.setCreatedAt(LocalDateTime.now());
                    reviewDetail.setUpdatedAt(LocalDateTime.now());

                    ReviewDetail saved = reviewDetailRepository.save(reviewDetail);
                    responses.add(ReviewDetailResponse.from(saved));
                }

                if (responses.isEmpty()) {
                    return ApiResponse.<List<ReviewDetailResponse>>builder()
                            .status("error")
                            .message("Failed to create any review detail")
                            .data(null)
                            .build();
                }

                return ApiResponse.<List<ReviewDetailResponse>>builder()
                        .status("success")
                        .message("✅ Review details created successfully!")
                        .data(responses)
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to create review details", e);
                return ApiResponse.<List<ReviewDetailResponse>>builder()
                        .status("error")
                        .message("Internal error while creating review details")
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<List<ReviewDetailResponse>> update(List<UpdateReviewDetailRequest> requests) {
        return tracingMetrics.traceAndMeasure("updateReviewDetails", "update_review_details", () -> {
            List<ReviewDetailResponse> responses = new ArrayList<>();
            try {
                validateRequest(requests);

                for (UpdateReviewDetailRequest req : requests) {
                    log.info("✏️ Updating review detail id={}", req.getReviewDetailId());

                    ReviewDetail reviewDetail = reviewDetailRepository.findById(String.valueOf(req.getReviewDetailId()))
                            .orElse(null);
                    if (reviewDetail == null) {
                        log.warn("Review detail not found id={}", req.getReviewDetailId());
                        continue;
                    }

                    reviewDetail.setType(req.getType());
                    reviewDetail.setUrl(req.getFile());
                    reviewDetail.setCaption(req.getCaption());
                    reviewDetail.setUpdatedAt(LocalDateTime.now());

                    ReviewDetail updated = reviewDetailRepository.save(reviewDetail);
                    responses.add(ReviewDetailResponse.from(updated));
                }

                if (responses.isEmpty()) {
                    return ApiResponse.<List<ReviewDetailResponse>>builder()
                            .status("error")
                            .message("Failed to update any review detail")
                            .data(null)
                            .build();
                }

                return ApiResponse.<List<ReviewDetailResponse>>builder()
                        .status("success")
                        .message("✅ Review details updated successfully!")
                        .data(responses)
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to update review details", e);
                return ApiResponse.<List<ReviewDetailResponse>>builder()
                        .status("error")
                        .message("Internal error while updating review details")
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<ReviewDetailResponseDeleteAt> trash(Integer reviewDetailId) {
        Attributes attrs = Attributes.builder()
                .put("review.detail.id", String.valueOf(reviewDetailId))
                .build();

        return tracingMetrics.traceAndMeasure("trashReviewDetail", "trash_review_detail", attrs, () -> {
            try {
                log.info("🗑️ Trashing review detail id={}", reviewDetailId);
                ReviewDetail trashed = reviewDetailRepository.trashed(String.valueOf(reviewDetailId));

                return ApiResponse.<ReviewDetailResponseDeleteAt>builder()
                        .status("success")
                        .message("🗑️ Review detail trashed successfully!")
                        .data(ReviewDetailResponseDeleteAt.from(trashed))
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to trash review detail id={}", reviewDetailId, e);
                return ApiResponse.<ReviewDetailResponseDeleteAt>builder()
                        .status("error")
                        .message("Failed to trash review detail: " + e.getMessage())
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<ReviewDetailResponseDeleteAt> restore(Integer reviewDetailId) {
        Attributes attrs = Attributes.builder()
                .put("review.detail.id", String.valueOf(reviewDetailId))
                .build();

        return tracingMetrics.traceAndMeasure("restoreReviewDetail", "restore_review_detail", attrs, () -> {
            try {
                log.info("♻️ Restoring review detail id={}", reviewDetailId);
                ReviewDetail restored = reviewDetailRepository.restore(String.valueOf(reviewDetailId));

                return ApiResponse.<ReviewDetailResponseDeleteAt>builder()
                        .status("success")
                        .message("♻️ Review detail restored successfully!")
                        .data(ReviewDetailResponseDeleteAt.from(restored))
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to restore review detail id={}", reviewDetailId, e);
                return ApiResponse.<ReviewDetailResponseDeleteAt>builder()
                        .status("error")
                        .message("Failed to restore review detail: " + e.getMessage())
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> delete(Integer reviewDetailId) {
        Attributes attrs = Attributes.builder()
                .put("review.detail.id", String.valueOf(reviewDetailId))
                .build();

        return tracingMetrics.traceAndMeasure("deleteReviewDetail", "delete_review_detail_permanent", attrs, () -> {
            try {
                log.info("🧨 Permanently deleting review detail id={}", reviewDetailId);
                reviewDetailRepository.deletePermanent(String.valueOf(reviewDetailId));

                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("🧨 Review detail permanently deleted!")
                        .data(true)
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to permanently delete review detail id={}", reviewDetailId, e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to permanently delete review detail: " + e.getMessage())
                        .data(false)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> restoreAll() {
        return tracingMetrics.traceAndMeasure("restoreAllReviewDetails", "restore_all_review_details", () -> {
            try {
                log.info("🔄 Restoring ALL trashed review details");
                reviewDetailRepository.restoreAllDeleted();

                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("🔄 All review details restored successfully!")
                        .data(true)
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to restore all review details", e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to restore all review details: " + e.getMessage())
                        .data(false)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> deleteAll() {
        return tracingMetrics.traceAndMeasure("deleteAllReviewDetails", "delete_all_review_details_permanent", () -> {
            try {
                log.info("💣 Permanently deleting ALL trashed review details");
                reviewDetailRepository.deleteAllDeleted();

                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("💣 All review details permanently deleted!")
                        .data(true)
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to delete all review details", e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to delete all review details: " + e.getMessage())
                        .data(false)
                        .build();
            }
        });
    }

    @Override
    public ApiResponsePagination<List<ReviewDetailResponse>> findAll(int page, int size, String search) {
        return tracingMetrics.traceAndMeasure("findAllReviewDetails", "find_all_review_details", () -> {
            int pageIndex = page > 0 ? page - 1 : 0;
            int pageSize = size > 0 ? size : 10;
            String keyword = (search != null && !search.isEmpty()) ? search : "";

            log.info("🔍 Searching all review details | Page: {}, Size: {}, Search: {}", pageIndex + 1, pageSize,
                    keyword.isEmpty() ? "None" : keyword);

            try {
                Pageable pageable = PageRequest.of(pageIndex, pageSize);
                Page<ReviewDetail> detailPage = reviewDetailRepository.findDetails(keyword, pageable);

                List<ReviewDetailResponse> responses = detailPage.getContent()
                        .stream()
                        .map(ReviewDetailResponse::from)
                        .toList();

                return ApiResponsePagination.<List<ReviewDetailResponse>>builder()
                        .status("success")
                        .message("Review details retrieved successfully")
                        .data(responses)
                        .pagination(PaginationMeta.fromSpringPage(detailPage))
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to fetch review details", e);
                return ApiResponsePagination.<List<ReviewDetailResponse>>builder()
                        .status("error")
                        .message("Failed to fetch review details")
                        .data(Collections.emptyList())
                        .pagination(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponsePagination<List<ReviewDetailResponseDeleteAt>> findByActive(int page, int size, String search) {
        return tracingMetrics.traceAndMeasure("findActiveReviewDetails", "find_active_review_details", () -> {
            int pageIndex = page > 0 ? page - 1 : 0;
            int pageSize = size > 0 ? size : 10;
            String keyword = (search != null && !search.isEmpty()) ? search : "";

            log.info("🔍 Searching active review details | Page: {}, Size: {}, Search: {}", pageIndex + 1, pageSize,
                    keyword.isEmpty() ? "None" : keyword);

            try {
                Pageable pageable = PageRequest.of(pageIndex, pageSize);
                Page<ReviewDetail> detailPage = reviewDetailRepository.findActiveDetails(keyword, pageable);

                List<ReviewDetailResponseDeleteAt> responses = detailPage.getContent()
                        .stream()
                        .map(ReviewDetailResponseDeleteAt::from)
                        .toList();

                return ApiResponsePagination.<List<ReviewDetailResponseDeleteAt>>builder()
                        .status("success")
                        .message("Active review details retrieved successfully")
                        .data(responses)
                        .pagination(PaginationMeta.fromSpringPage(detailPage))
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to fetch active review details", e);
                return ApiResponsePagination.<List<ReviewDetailResponseDeleteAt>>builder()
                        .status("error")
                        .message("Failed to fetch active review details")
                        .data(Collections.emptyList())
                        .pagination(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponsePagination<List<ReviewDetailResponseDeleteAt>> findByTrashed(int page, int size, String search) {
        return tracingMetrics.traceAndMeasure("findTrashedReviewDetails", "find_trashed_review_details", () -> {
            int pageIndex = page > 0 ? page - 1 : 0;
            int pageSize = size > 0 ? size : 10;
            String keyword = (search != null && !search.isEmpty()) ? search : "";

            log.info("🔍 Searching trashed review details | Page: {}, Size: {}, Search: {}", pageIndex + 1, pageSize,
                    keyword.isEmpty() ? "None" : keyword);

            try {
                Pageable pageable = PageRequest.of(pageIndex, pageSize);
                Page<ReviewDetail> detailPage = reviewDetailRepository.findTrashedDetails(keyword, pageable);

                List<ReviewDetailResponseDeleteAt> responses = detailPage.getContent()
                        .stream()
                        .map(ReviewDetailResponseDeleteAt::from)
                        .toList();

                return ApiResponsePagination.<List<ReviewDetailResponseDeleteAt>>builder()
                        .status("success")
                        .message("Trashed review details retrieved successfully")
                        .data(responses)
                        .pagination(PaginationMeta.fromSpringPage(detailPage))
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to fetch trashed review details", e);
                return ApiResponsePagination.<List<ReviewDetailResponseDeleteAt>>builder()
                        .status("error")
                        .message("Failed to fetch trashed review details")
                        .data(Collections.emptyList())
                        .pagination(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<ReviewDetailResponse> findById(Integer reviewDetailId) {
        Attributes attrs = Attributes.builder()
                .put("review.detail.id", String.valueOf(reviewDetailId))
                .build();

        return tracingMetrics.traceAndMeasure("findReviewDetailById", "find_review_detail_by_id", attrs, () -> {
            log.info("🔍 Finding review detail by id={}", reviewDetailId);
            try {
                return reviewDetailRepository.findById(String.valueOf(reviewDetailId))
                        .map(detail -> ApiResponse.<ReviewDetailResponse>builder()
                                .status("success")
                                .message("Review detail retrieved successfully")
                                .data(ReviewDetailResponse.from(detail))
                                .build())
                        .orElseGet(() -> {
                            log.warn("❌ Review detail not found with id={}", reviewDetailId);
                            return ApiResponse.<ReviewDetailResponse>builder()
                                    .status("error")
                                    .message("Review detail not found")
                                    .data(null)
                                    .build();
                        });
            } catch (Exception e) {
                log.error("💥 Failed to fetch review detail by id={}", reviewDetailId, e);
                return ApiResponse.<ReviewDetailResponse>builder()
                        .status("error")
                        .message("Failed to fetch review detail")
                        .data(null)
                        .build();
            }
        });
    }

    private <T> void validateRequest(T req) {
        Set<ConstraintViolation<T>> violations = validator.validate(req);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<T> violation : violations) {
                sb.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("; ");
            }
            log.error("Validation failed: {}", sb);
            throw new ConstraintViolationException("Validation failed: " + sb, violations);
        }
    }
}
