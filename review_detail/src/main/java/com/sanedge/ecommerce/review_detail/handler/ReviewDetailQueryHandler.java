package com.sanedge.ecommerce.review_detail.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.protobuf.StringValue;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.pb.review.FindAllReviewRequest;
import com.sanedge.ecommerce.pb.review_detail.ApiResponsePaginationReviewDetails;
import com.sanedge.ecommerce.pb.review_detail.ApiResponsePaginationReviewDetailsDeleteAt;
import com.sanedge.ecommerce.pb.review_detail.ApiResponseReviewDetail;
import com.sanedge.ecommerce.pb.review_detail.FindByIdReviewDetailRequest;
import com.sanedge.ecommerce.pb.review_detail.ReviewDetailQueryServiceGrpc;
import com.sanedge.ecommerce.pb.review_detail.ReviewDetailsResponse;
import com.sanedge.ecommerce.pb.review_detail.ReviewDetailsResponseDeleteAt;
import com.sanedge.ecommerce.review_detail.service.reviewdetail.ReviewDetailService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewDetailQueryHandler extends ReviewDetailQueryServiceGrpc.ReviewDetailQueryServiceImplBase {

    private final ReviewDetailService reviewDetailService;

    @Override
    public void findAll(FindAllReviewRequest request, StreamObserver<ApiResponsePaginationReviewDetails> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.review_detail.domain.responses.reviewdetail.ReviewDetailResponse>> res = reviewDetailService
                    .findAll(request.getPage(), request.getPageSize(), request.getSearch());

            ApiResponsePaginationReviewDetails.Builder builder = ApiResponsePaginationReviewDetails.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setPagination(ProtoMappers.toProtoPagination(res.getPagination()));
            if (res.getData() != null) {
                res.getData().forEach(r -> builder.addData(toProto(r)));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findById(FindByIdReviewDetailRequest request, StreamObserver<ApiResponseReviewDetail> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.review_detail.domain.responses.reviewdetail.ReviewDetailResponse> res = reviewDetailService
                    .findById(request.getId());

            ApiResponseReviewDetail.Builder builder = ApiResponseReviewDetail.newBuilder()
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
    public void findByActive(FindAllReviewRequest request,
            StreamObserver<ApiResponsePaginationReviewDetailsDeleteAt> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.review_detail.domain.responses.reviewdetail.ReviewDetailResponseDeleteAt>> res = reviewDetailService
                    .findByActive(request.getPage(), request.getPageSize(), request.getSearch());

            ApiResponsePaginationReviewDetailsDeleteAt.Builder builder = ApiResponsePaginationReviewDetailsDeleteAt
                    .newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setPagination(ProtoMappers.toProtoPagination(res.getPagination()));
            if (res.getData() != null) {
                res.getData().forEach(r -> builder.addData(toProtoDeleteAt(r)));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findByTrashed(FindAllReviewRequest request,
            StreamObserver<ApiResponsePaginationReviewDetailsDeleteAt> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.review_detail.domain.responses.reviewdetail.ReviewDetailResponseDeleteAt>> res = reviewDetailService
                    .findByTrashed(request.getPage(), request.getPageSize(), request.getSearch());

            ApiResponsePaginationReviewDetailsDeleteAt.Builder builder = ApiResponsePaginationReviewDetailsDeleteAt
                    .newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setPagination(ProtoMappers.toProtoPagination(res.getPagination()));
            if (res.getData() != null) {
                res.getData().forEach(r -> builder.addData(toProtoDeleteAt(r)));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
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
