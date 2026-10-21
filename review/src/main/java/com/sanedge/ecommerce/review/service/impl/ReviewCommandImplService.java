package com.sanedge.ecommerce.review.service.impl;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.exception.ResourceNotFoundException;
import com.sanedge.ecommerce.common.observability.TracingMetrics;
import com.sanedge.ecommerce.review.domain.requests.CreateReviewRequest;
import com.sanedge.ecommerce.review.domain.requests.UpdateReviewRequest;
import com.sanedge.ecommerce.review.domain.responses.ReviewResponse;
import com.sanedge.ecommerce.review.domain.responses.ReviewResponseDeleteAt;
import com.sanedge.ecommerce.review.models.Review;
import com.sanedge.ecommerce.review.repository.ReviewCommandRepository;
import com.sanedge.ecommerce.review.repository.ReviewQueryRepository;
import com.sanedge.ecommerce.review.service.ReviewCommandService;

import io.opentelemetry.api.common.Attributes;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewCommandImplService implements ReviewCommandService {
    private final ReviewQueryRepository reviewQueryRepository;
    private final ReviewCommandRepository reviewCommandRepository;
    private final Validator validator;
    private final TracingMetrics tracingMetrics;

    @Override
    public ApiResponse<ReviewResponse> create(CreateReviewRequest request) {
        Attributes attrs = Attributes.builder()
                .put("product.id", String.valueOf(request.getProductId()))
                .put("user.id", String.valueOf(request.getUserId()))
                .build();

        return tracingMetrics.traceAndMeasure("createReview", "create_review", attrs, () -> {
            try {
                validateRequest(request);

                log.info("🆕 Creating review for productId={}, userId={}",
                        request.getProductId(), request.getUserId());

                Review review = new Review();
                review.setUserId(request.getUserId());
                review.setProductId(request.getProductId());
                review.setRating(request.getRating());
                review.setComment(request.getComment());
                review.setCreatedAt(LocalDateTime.now());
                review.setUpdatedAt(LocalDateTime.now());

                Review savedReview = reviewCommandRepository.save(review);
                ReviewResponse response = ReviewResponse.from(savedReview);

                log.info("✅ Review created successfully id={}", response.getId());

                return ApiResponse.<ReviewResponse>builder()
                        .status("success")
                        .message("✅ Review created successfully!")
                        .data(response)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to create review", e);
                return ApiResponse.<ReviewResponse>builder()
                        .status("error")
                        .message(e.getMessage())
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<ReviewResponse> update(UpdateReviewRequest request) {
        Attributes attrs = Attributes.builder()
                .put("review.id", String.valueOf(request.getReviewId()))
                .build();

        return tracingMetrics.traceAndMeasure("updateReview", "update_review", attrs, () -> {
            try {
                validateRequest(request);

                if (request.getReviewId() == null) {
                    throw new ResourceNotFoundException("review_id is required");
                }

                log.info("🔄 Updating review id={}", request.getReviewId());

                Review review = reviewQueryRepository.findById(String.valueOf(request.getReviewId()))
                        .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

                review.setComment(request.getComment());
                review.setRating(request.getRating());
                review.setUpdatedAt(LocalDateTime.now());

                Review updatedReview = reviewCommandRepository.save(review);
                ReviewResponse response = ReviewResponse.from(updatedReview);

                log.info("✅ Review updated successfully id={}", response.getId());

                return ApiResponse.<ReviewResponse>builder()
                        .status("success")
                        .message("✅ Review updated successfully!")
                        .data(response)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to update review id={}", request.getReviewId(), e);
                return ApiResponse.<ReviewResponse>builder()
                        .status("error")
                        .message(e.getMessage())
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<ReviewResponseDeleteAt> trash(Integer id) {
        Attributes attrs = Attributes.builder()
                .put("review.id", String.valueOf(id))
                .build();

        return tracingMetrics.traceAndMeasure("trashReview", "trash_review", attrs, () -> {
            log.info("🗑️ Trashing review id={}", id);
            try {
                Review review = reviewCommandRepository.trashed(String.valueOf(id));
                return ApiResponse.<ReviewResponseDeleteAt>builder()
                        .status("success")
                        .message("🗑️ Review trashed successfully!")
                        .data(ReviewResponseDeleteAt.from(review))
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to trash review id={}", id, e);
                return ApiResponse.<ReviewResponseDeleteAt>builder()
                        .status("error")
                        .message("Failed to trash review: " + e.getMessage())
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<ReviewResponseDeleteAt> restore(Integer id) {
        Attributes attrs = Attributes.builder()
                .put("review.id", String.valueOf(id))
                .build();

        return tracingMetrics.traceAndMeasure("restoreReview", "restore_review", attrs, () -> {
            log.info("♻️ Restoring review id={}", id);
            try {
                Review review = reviewCommandRepository.restore(String.valueOf(id));
                return ApiResponse.<ReviewResponseDeleteAt>builder()
                        .status("success")
                        .message("♻️ Review restored successfully!")
                        .data(ReviewResponseDeleteAt.from(review))
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to restore review id={}", id, e);
                return ApiResponse.<ReviewResponseDeleteAt>builder()
                        .status("error")
                        .message("Failed to restore review: " + e.getMessage())
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> delete(Integer id) {
        Attributes attrs = Attributes.builder()
                .put("review.id", String.valueOf(id))
                .build();

        return tracingMetrics.traceAndMeasure("deleteReviewPermanent", "delete_review_permanent", attrs, () -> {
            log.info("🧨 Permanently deleting review id={}", id);
            try {
                reviewCommandRepository.deletePermanent(String.valueOf(id));
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("🧨 Review permanently deleted!")
                        .data(true)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to permanently delete review id={}", id, e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to permanently delete review: " + e.getMessage())
                        .data(false)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> restoreAll() {
        return tracingMetrics.traceAndMeasure("restoreAllReviews", "restore_all_reviews", () -> {
            log.info("🔄 Restoring ALL trashed reviews");
            try {
                reviewCommandRepository.restoreAllDeleted();
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("🔄 All reviews restored successfully!")
                        .data(true)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to restore all reviews", e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to restore all reviews: " + e.getMessage())
                        .data(false)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> deleteAll() {
        return tracingMetrics.traceAndMeasure("deleteAllReviewsPermanent", "delete_all_reviews_permanent", () -> {
            log.info("💣 Permanently deleting ALL trashed reviews");
            try {
                reviewCommandRepository.deleteAllDeleted();
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("💣 All reviews permanently deleted!")
                        .data(true)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to delete all reviews", e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to delete all reviews: " + e.getMessage())
                        .data(false)
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
