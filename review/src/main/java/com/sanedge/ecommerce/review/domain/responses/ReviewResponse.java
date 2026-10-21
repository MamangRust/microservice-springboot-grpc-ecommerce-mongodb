package com.sanedge.ecommerce.review.domain.responses;

import com.sanedge.ecommerce.review.models.Review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private String name;
    private String comment;
    private Integer rating;
    private String createdAt;
    private String updatedAt;

    public static ReviewResponse from(Review review) {
        return ReviewResponse.builder()
                .id(toIntId(review.getReviewId()))
                .userId(review.getUserId())
                .productId(review.getProductId())
                .name(review.getName())
                .comment(review.getComment())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt() != null ? review.getCreatedAt().toString() : null)
                .updatedAt(review.getUpdatedAt() != null ? review.getUpdatedAt().toString() : null)
                .build();
    }

    private static Integer toIntId(String id) {
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
