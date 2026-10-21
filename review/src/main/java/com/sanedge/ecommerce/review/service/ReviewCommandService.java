package com.sanedge.ecommerce.review.service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.review.domain.requests.CreateReviewRequest;
import com.sanedge.ecommerce.review.domain.requests.UpdateReviewRequest;
import com.sanedge.ecommerce.review.domain.responses.ReviewResponse;
import com.sanedge.ecommerce.review.domain.responses.ReviewResponseDeleteAt;

public interface ReviewCommandService {
    ApiResponse<ReviewResponse> create(CreateReviewRequest request);

    ApiResponse<ReviewResponse> update(UpdateReviewRequest request);

    ApiResponse<ReviewResponseDeleteAt> trash(Integer id);

    ApiResponse<ReviewResponseDeleteAt> restore(Integer id);

    ApiResponse<Boolean> delete(Integer id);

    ApiResponse<Boolean> restoreAll();

    ApiResponse<Boolean> deleteAll();
}
