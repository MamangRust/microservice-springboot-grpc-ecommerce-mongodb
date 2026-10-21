package com.sanedge.ecommerce.review_detail.domain.responses.reviewdetail;

import com.sanedge.ecommerce.review_detail.models.review.ReviewDetail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDetailResponseDeleteAt {
    private int id;
    private int reviewId;
    private String type;
    private String url;
    private String caption;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;

    public static ReviewDetailResponseDeleteAt from(ReviewDetail reviewDetail) {
        if (reviewDetail == null) {
            return null;
        }
        return ReviewDetailResponseDeleteAt.builder()
                .id(toIntId(reviewDetail.getReviewDetailId()))
                .reviewId(reviewDetail.getReviewId())
                .type(reviewDetail.getType())
                .url(reviewDetail.getUrl())
                .caption(reviewDetail.getCaption())
                .createdAt(reviewDetail.getCreatedAt() != null ? reviewDetail.getCreatedAt().toString() : null)
                .updatedAt(reviewDetail.getUpdatedAt() != null ? reviewDetail.getUpdatedAt().toString() : null)
                .deletedAt(reviewDetail.getDeletedAt() != null ? reviewDetail.getDeletedAt().toString() : null)
                .build();
    }

    private static int toIntId(String id) {
        if (id == null) {
            return 0;
        }
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
