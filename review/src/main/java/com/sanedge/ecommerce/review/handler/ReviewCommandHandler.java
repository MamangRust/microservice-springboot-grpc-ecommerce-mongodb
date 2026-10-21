package com.sanedge.ecommerce.review.handler;

import org.springframework.stereotype.Service;

import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.pb.review.ApiResponseReview;
import com.sanedge.ecommerce.pb.review.ApiResponseReviewAll;
import com.sanedge.ecommerce.pb.review.ApiResponseReviewDelete;
import com.sanedge.ecommerce.pb.review.ApiResponseReviewDeleteAt;
import com.sanedge.ecommerce.pb.review.CreateReviewRequest;
import com.sanedge.ecommerce.pb.review.FindByIdReviewRequest;
import com.sanedge.ecommerce.pb.review.ReviewCommandServiceGrpc;
import com.sanedge.ecommerce.pb.review.ReviewResponse;
import com.sanedge.ecommerce.pb.review.ReviewResponseDeleteAt;
import com.sanedge.ecommerce.pb.review.UpdateReviewRequest;
import com.sanedge.ecommerce.review.service.ReviewCommandService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewCommandHandler extends ReviewCommandServiceGrpc.ReviewCommandServiceImplBase {

    private final ReviewCommandService reviewCommandService;

    @Override
    public void create(CreateReviewRequest request, StreamObserver<ApiResponseReview> observer) {
        try {
            com.sanedge.ecommerce.review.domain.requests.CreateReviewRequest req = new com.sanedge.ecommerce.review.domain.requests.CreateReviewRequest();
            req.setUserId(request.getUserId());
            req.setProductId(request.getProductId());
            req.setRating(request.getRating());
            req.setComment(request.getComment());

            ApiResponse<com.sanedge.ecommerce.review.domain.responses.ReviewResponse> res = reviewCommandService
                    .create(req);

            ApiResponseReview.Builder builder = ApiResponseReview.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            if (res.getData() != null) {
                builder.setData(toProto(res.getData()));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void update(UpdateReviewRequest request, StreamObserver<ApiResponseReview> observer) {
        try {
            com.sanedge.ecommerce.review.domain.requests.UpdateReviewRequest req = new com.sanedge.ecommerce.review.domain.requests.UpdateReviewRequest();
            req.setReviewId(request.getReviewId());
            req.setName(request.getName());
            req.setRating(request.getRating());
            req.setComment(request.getComment());

            ApiResponse<com.sanedge.ecommerce.review.domain.responses.ReviewResponse> res = reviewCommandService
                    .update(req);

            ApiResponseReview.Builder builder = ApiResponseReview.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            if (res.getData() != null) {
                builder.setData(toProto(res.getData()));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void trashedReview(FindByIdReviewRequest request, StreamObserver<ApiResponseReviewDeleteAt> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.review.domain.responses.ReviewResponseDeleteAt> res = reviewCommandService
                    .trash(request.getId());
            observer.onNext(toDeleteAt(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void restoreReview(FindByIdReviewRequest request, StreamObserver<ApiResponseReviewDeleteAt> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.review.domain.responses.ReviewResponseDeleteAt> res = reviewCommandService
                    .restore(request.getId());
            observer.onNext(toDeleteAt(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteReviewPermanent(FindByIdReviewRequest request, StreamObserver<ApiResponseReviewDelete> observer) {
        try {
            ApiResponse<Boolean> res = reviewCommandService.delete(request.getId());
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
    public void restoreAllReview(Empty request, StreamObserver<ApiResponseReviewAll> observer) {
        try {
            ApiResponse<Boolean> res = reviewCommandService.restoreAll();
            observer.onNext(toAll(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteAllReviewPermanent(Empty request, StreamObserver<ApiResponseReviewAll> observer) {
        try {
            ApiResponse<Boolean> res = reviewCommandService.deleteAll();
            observer.onNext(toAll(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private ApiResponseReviewDeleteAt toDeleteAt(
            ApiResponse<com.sanedge.ecommerce.review.domain.responses.ReviewResponseDeleteAt> res) {
        ApiResponseReviewDeleteAt.Builder builder = ApiResponseReviewDeleteAt.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()));
        if (res.getData() != null) {
            builder.setData(toProto(res.getData()));
        }
        return builder.build();
    }

    private ApiResponseReviewAll toAll(ApiResponse<Boolean> res) {
        return ApiResponseReviewAll.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()))
                .build();
    }

    private ReviewResponse toProto(com.sanedge.ecommerce.review.domain.responses.ReviewResponse r) {
        return ReviewResponse.newBuilder()
                .setId(r.getId() == null ? 0 : r.getId())
                .setUserId(r.getUserId() == null ? 0 : r.getUserId())
                .setProductId(r.getProductId() == null ? 0 : r.getProductId())
                .setName(ProtoMappers.nvl(r.getName()))
                .setComment(ProtoMappers.nvl(r.getComment()))
                .setRating(r.getRating() == null ? 0 : r.getRating())
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .build();
    }

    private ReviewResponseDeleteAt toProto(
            com.sanedge.ecommerce.review.domain.responses.ReviewResponseDeleteAt r) {
        return ReviewResponseDeleteAt.newBuilder()
                .setId(r.getId() == null ? 0 : r.getId())
                .setUserId(r.getUserId() == null ? 0 : r.getUserId())
                .setProductId(r.getProductId() == null ? 0 : r.getProductId())
                .setName(ProtoMappers.nvl(r.getName()))
                .setComment(ProtoMappers.nvl(r.getComment()))
                .setRating(r.getRating() == null ? 0 : r.getRating())
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .setDeletedAt(StringValue.of(ProtoMappers.nvl(r.getDeletedAt())))
                .build();
    }
}
