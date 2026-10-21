package com.sanedge.ecommerce.review_detail.service.reviewdetail;

import java.util.List;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.review_detail.domain.requests.reviewdetail.CreateReviewDetailRequest;
import com.sanedge.ecommerce.review_detail.domain.requests.reviewdetail.UpdateReviewDetailRequest;
import com.sanedge.ecommerce.review_detail.domain.responses.reviewdetail.ReviewDetailResponse;
import com.sanedge.ecommerce.review_detail.domain.responses.reviewdetail.ReviewDetailResponseDeleteAt;

public interface ReviewDetailService {
    ApiResponse<List<ReviewDetailResponse>> create(List<CreateReviewDetailRequest> request);

    ApiResponse<List<ReviewDetailResponse>> update(List<UpdateReviewDetailRequest> request);

    ApiResponse<ReviewDetailResponseDeleteAt> trash(Integer reviewDetailId);

    ApiResponse<ReviewDetailResponseDeleteAt> restore(Integer reviewDetailId);

    ApiResponse<Boolean> delete(Integer reviewDetailId);

    ApiResponse<Boolean> restoreAll();

    ApiResponse<Boolean> deleteAll();

    ApiResponsePagination<List<ReviewDetailResponse>> findAll(int page, int size, String search);

    ApiResponsePagination<List<ReviewDetailResponseDeleteAt>> findByActive(int page, int size, String search);

    ApiResponsePagination<List<ReviewDetailResponseDeleteAt>> findByTrashed(int page, int size, String search);

    ApiResponse<ReviewDetailResponse> findById(Integer reviewDetailId);
}
