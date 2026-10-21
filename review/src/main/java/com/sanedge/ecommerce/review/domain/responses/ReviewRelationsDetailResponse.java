package com.sanedge.ecommerce.review.domain.responses;

import java.util.List;

import com.sanedge.ecommerce.review.models.ReviewRelationsDetail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRelationsDetailResponse {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private String name;
    private String comment;
    private Integer rating;
    private List<ReviewDetailResponse> reviewDetail;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;

    public static ReviewRelationsDetailResponse from(ReviewRelationsDetail response) {
        return ReviewRelationsDetailResponse.builder()
                .id(response.getId())
                .userId(response.getUserId())
                .productId(response.getProductId())
                .name(response.getName())
                .comment(response.getComment())
                .rating(response.getRating())
                .reviewDetail(
                        response.getReviewDetail() != null
                                ? ReviewDetailResponse.fromList(response.getReviewDetail())
                                : List.of())
                .createdAt(response.getCreatedAt())
                .updatedAt(response.getUpdatedAt())
                .deletedAt(response.getDeletedAt() != null ? response.getDeletedAt() : null)
                .build();
    }
}
