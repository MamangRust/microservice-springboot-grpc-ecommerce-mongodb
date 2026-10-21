package com.sanedge.ecommerce.review.service;

import java.util.List;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.review.domain.requests.FindAllReview;
import com.sanedge.ecommerce.review.domain.requests.FindAllReviewByMerchant;
import com.sanedge.ecommerce.review.domain.requests.FindAllReviewByProduct;
import com.sanedge.ecommerce.review.domain.responses.ReviewRelationsDetailResponse;
import com.sanedge.ecommerce.review.domain.responses.ReviewResponse;
import com.sanedge.ecommerce.review.domain.responses.ReviewResponseDeleteAt;

public interface ReviewQueryService {
    ApiResponsePagination<List<ReviewResponse>> findAll(FindAllReview findAllReview);

    ApiResponsePagination<List<ReviewResponseDeleteAt>> findActive(FindAllReview findAllReview);

    ApiResponsePagination<List<ReviewResponseDeleteAt>> findTrashed(FindAllReview findAllReview);

    ApiResponsePagination<List<ReviewRelationsDetailResponse>> findByMerchant(
            FindAllReviewByMerchant findAllReviewByMerchant);

    ApiResponsePagination<List<ReviewRelationsDetailResponse>> findByProduct(
            FindAllReviewByProduct findAllReviewByProduct);

    ApiResponse<ReviewResponse> findById(Integer reviewId);
}
