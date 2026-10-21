package com.sanedge.ecommerce.review_detail.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.pb.review.ApiResponseReviewAll;
import com.sanedge.ecommerce.pb.review.ApiResponseReviewDelete;
import com.sanedge.ecommerce.pb.review_detail.ApiResponseReviewDetail;
import com.sanedge.ecommerce.pb.review_detail.ApiResponseReviewDetailDeleteAt;
import com.sanedge.ecommerce.pb.review_detail.CreateReviewDetailRequest;
import com.sanedge.ecommerce.pb.review_detail.FindByIdReviewDetailRequest;
import com.sanedge.ecommerce.pb.review_detail.ReviewDetailCommandServiceGrpc;
import com.sanedge.ecommerce.pb.review_detail.ReviewDetailsResponse;
import com.sanedge.ecommerce.pb.review_detail.ReviewDetailsResponseDeleteAt;
import com.sanedge.ecommerce.pb.review_detail.UpdateReviewDetailRequest;
import com.sanedge.ecommerce.review_detail.service.reviewdetail.ReviewDetailService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewDetailCommandHandler extends ReviewDetailCommandServiceGrpc.ReviewDetailCommandServiceImplBase {

    private final ReviewDetailService reviewDetailService;

    @Override
    public void create(CreateReviewDetailRequest request, StreamObserver<ApiResponseReviewDetail> observer) {
        try {
            com.sanedge.ecommerce.review_detail.domain.requests.reviewdetail.CreateReviewDetailRequest req = new com.sanedge.ecommerce.review_detail.domain.requests.reviewdetail.CreateReviewDetailRequest();
            req.setReviewId(request.getReviewId());
            req.setType(request.getType());
            req.setFile(request.getUrl());
            req.setCaption(request.getCaption());

            ApiResponse<List<com.sanedge.ecommerce.review_detail.domain.responses.reviewdetail.ReviewDetailResponse>> res = reviewDetailService
                    .create(List.of(req));

            ApiResponseReviewDetail.Builder builder = ApiResponseReviewDetail.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            if (res.getData() != null && !res.getData().isEmpty()) {
                builder.setData(toProto(res.getData().get(0)));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void update(UpdateReviewDetailRequest request, StreamObserver<ApiResponseReviewDetail> observer) {
        try {
            com.sanedge.ecommerce.review_detail.domain.requests.reviewdetail.UpdateReviewDetailRequest req = new com.sanedge.ecommerce.review_detail.domain.requests.reviewdetail.UpdateReviewDetailRequest();
            req.setReviewDetailId(request.getReviewDetailId());
            req.setType(request.getType());
            req.setFile(request.getUrl());
            req.setCaption(request.getCaption());

            ApiResponse<List<com.sanedge.ecommerce.review_detail.domain.responses.reviewdetail.ReviewDetailResponse>> res = reviewDetailService
                    .update(List.of(req));

            ApiResponseReviewDetail.Builder builder = ApiResponseReviewDetail.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            if (res.getData() != null && !res.getData().isEmpty()) {
                builder.setData(toProto(res.getData().get(0)));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void trashedReviewDetail(FindByIdReviewDetailRequest request,
            StreamObserver<ApiResponseReviewDetailDeleteAt> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.review_detail.domain.responses.reviewdetail.ReviewDetailResponseDeleteAt> res = reviewDetailService
                    .trash(request.getId());
            observer.onNext(toDeleteAt(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void restoreReviewDetail(FindByIdReviewDetailRequest request,
            StreamObserver<ApiResponseReviewDetailDeleteAt> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.review_detail.domain.responses.reviewdetail.ReviewDetailResponseDeleteAt> res = reviewDetailService
                    .restore(request.getId());
            observer.onNext(toDeleteAt(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteReviewDetailPermanent(FindByIdReviewDetailRequest request,
            StreamObserver<ApiResponseReviewDelete> observer) {
        try {
            ApiResponse<Boolean> res = reviewDetailService.delete(request.getId());
            observer.onNext(ApiResponseReviewDelete.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void restoreAllReviewDetail(Empty request, StreamObserver<ApiResponseReviewAll> observer) {
        try {
            ApiResponse<Boolean> res = reviewDetailService.restoreAll();
            observer.onNext(toAll(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteAllReviewDetailPermanent(Empty request, StreamObserver<ApiResponseReviewAll> observer) {
        try {
            ApiResponse<Boolean> res = reviewDetailService.deleteAll();
            observer.onNext(toAll(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private ApiResponseReviewDetailDeleteAt toDeleteAt(
            ApiResponse<com.sanedge.ecommerce.review_detail.domain.responses.reviewdetail.ReviewDetailResponseDeleteAt> res) {
        ApiResponseReviewDetailDeleteAt.Builder builder = ApiResponseReviewDetailDeleteAt.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()));
        if (res.getData() != null) {
            builder.setData(toProtoDeleteAt(res.getData()));
        }
        return builder.build();
    }

    private ApiResponseReviewAll toAll(ApiResponse<Boolean> res) {
        return ApiResponseReviewAll.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()))
                .build();
    }

    private ReviewDetailsResponse toProto(
            com.sanedge.ecommerce.review_detail.domain.responses.reviewdetail.ReviewDetailResponse r) {
        return ReviewDetailsResponse.newBuilder()
                .setId(r.getId())
                .setReviewId(r.getReviewId())
                .setType(ProtoMappers.nvl(r.getType()))
                .setUrl(ProtoMappers.nvl(r.getUrl()))
                .setCaption(ProtoMappers.nvl(r.getCaption()))
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .build();
    }

    private ReviewDetailsResponseDeleteAt toProtoDeleteAt(
            com.sanedge.ecommerce.review_detail.domain.responses.reviewdetail.ReviewDetailResponseDeleteAt r) {
        return ReviewDetailsResponseDeleteAt.newBuilder()
                .setId(r.getId())
                .setReviewId(r.getReviewId())
                .setType(ProtoMappers.nvl(r.getType()))
                .setUrl(ProtoMappers.nvl(r.getUrl()))
                .setCaption(ProtoMappers.nvl(r.getCaption()))
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .setDeletedAt(StringValue.of(ProtoMappers.nvl(r.getDeletedAt())))
                .build();
    }
}
