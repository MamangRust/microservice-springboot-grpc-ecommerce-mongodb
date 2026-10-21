package com.sanedge.ecommerce.gateway.dto.responses.reviews;

import java.util.List;

import com.sanedge.ecommerce.gateway.dto.responses.reviewdetail.ReviewDetailResponse;

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

}