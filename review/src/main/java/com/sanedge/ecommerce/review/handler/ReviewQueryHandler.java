package com.sanedge.ecommerce.review.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.pb.review.ApiResponsePaginationReview;
import com.sanedge.ecommerce.pb.review.ApiResponsePaginationReviewDeleteAt;
import com.sanedge.ecommerce.pb.review.ApiResponsePaginationReviewDetail;
import com.sanedge.ecommerce.pb.review.ApiResponseReview;
import com.sanedge.ecommerce.pb.review.FindAllReviewMerchantRequest;
import com.sanedge.ecommerce.pb.review.FindAllReviewProductRequest;
import com.sanedge.ecommerce.pb.review.FindAllReviewRequest;
import com.sanedge.ecommerce.pb.review.FindByIdReviewRequest;
import com.sanedge.ecommerce.pb.review.ReviewDetailResponse;
import com.sanedge.ecommerce.pb.review.ReviewQueryServiceGrpc;
import com.sanedge.ecommerce.pb.review.ReviewResponse;
import com.sanedge.ecommerce.pb.review.ReviewResponseDeleteAt;
import com.sanedge.ecommerce.pb.review.ReviewsDetailResponse;
import com.sanedge.ecommerce.review.domain.requests.FindAllReview;
import com.sanedge.ecommerce.review.domain.requests.FindAllReviewByMerchant;
import com.sanedge.ecommerce.review.domain.requests.FindAllReviewByProduct;
import com.sanedge.ecommerce.review.service.ReviewQueryService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewQueryHandler extends ReviewQueryServiceGrpc.ReviewQueryServiceImplBase {

    private final ReviewQueryService reviewQueryService;

    @Override
    public void findAll(FindAllReviewRequest request, StreamObserver<ApiResponsePaginationReview> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.review.domain.responses.ReviewResponse>> res = reviewQueryService
                    .findAll(toFindAll(request));

            ApiResponsePaginationReview.Builder builder = ApiResponsePaginationReview.newBuilder()
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
    public void findByProduct(FindAllReviewProductRequest request,
            StreamObserver<ApiResponsePaginationReviewDetail> observer) {
        try {
            FindAllReviewByProduct req = new FindAllReviewByProduct();
            req.setProductId(request.getProductId());
            req.setPage(request.getPage());
            req.setPageSize(request.getPageSize());
            req.setSearch(request.getSearch());

            ApiResponsePagination<List<com.sanedge.ecommerce.review.domain.responses.ReviewRelationsDetailResponse>> res = reviewQueryService
                    .findByProduct(req);

            ApiResponsePaginationReviewDetail.Builder builder = ApiResponsePaginationReviewDetail.newBuilder()
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
    public void findByMerchant(FindAllReviewMerchantRequest request,
            StreamObserver<ApiResponsePaginationReviewDetail> observer) {
        try {
            FindAllReviewByMerchant req = new FindAllReviewByMerchant();
            req.setMerchantId(request.getMerchantId());
            req.setPage(request.getPage());
            req.setPageSize(request.getPageSize());
            req.setSearch(request.getSearch());

            ApiResponsePagination<List<com.sanedge.ecommerce.review.domain.responses.ReviewRelationsDetailResponse>> res = reviewQueryService
                    .findByMerchant(req);

            ApiResponsePaginationReviewDetail.Builder builder = ApiResponsePaginationReviewDetail.newBuilder()
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
    public void findByActive(FindAllReviewRequest request,
            StreamObserver<ApiResponsePaginationReviewDeleteAt> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.review.domain.responses.ReviewResponseDeleteAt>> res = reviewQueryService
                    .findActive(toFindAll(request));

            ApiResponsePaginationReviewDeleteAt.Builder builder = ApiResponsePaginationReviewDeleteAt.newBuilder()
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
            StreamObserver<ApiResponsePaginationReviewDeleteAt> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.review.domain.responses.ReviewResponseDeleteAt>> res = reviewQueryService
                    .findTrashed(toFindAll(request));

            ApiResponsePaginationReviewDeleteAt.Builder builder = ApiResponsePaginationReviewDeleteAt.newBuilder()
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
    public void findById(FindByIdReviewRequest request, StreamObserver<ApiResponseReview> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.review.domain.responses.ReviewResponse> res = reviewQueryService
                    .findById(request.getId());

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

    private FindAllReview toFindAll(FindAllReviewRequest request) {
        FindAllReview req = new FindAllReview();
        req.setPage(request.getPage());
        req.setPageSize(request.getPageSize());
        req.setSearch(request.getSearch());
        return req;
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

    private ReviewResponseDeleteAt toProtoDeleteAt(
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
                .setDeletedAt(com.google.protobuf.StringValue.of(ProtoMappers.nvl(r.getDeletedAt())))
                .build();
    }

    private ReviewsDetailResponse toProto(
            com.sanedge.ecommerce.review.domain.responses.ReviewRelationsDetailResponse r) {
        ReviewsDetailResponse.Builder builder = ReviewsDetailResponse.newBuilder()
                .setId(r.getId() == null ? 0 : r.getId())
                .setUserId(r.getUserId() == null ? 0 : r.getUserId())
                .setProductId(r.getProductId() == null ? 0 : r.getProductId())
                .setName(ProtoMappers.nvl(r.getName()))
                .setComment(ProtoMappers.nvl(r.getComment()))
                .setRating(r.getRating() == null ? 0 : r.getRating())
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .setDeletedAt(ProtoMappers.nvl(r.getDeletedAt()));
        if (r.getReviewDetail() != null && !r.getReviewDetail().isEmpty()) {
            builder.setReviewDetail(toProto(r.getReviewDetail().get(0)));
        }
        return builder.build();
    }

    private ReviewDetailResponse toProto(com.sanedge.ecommerce.review.domain.responses.ReviewDetailResponse r) {
        return ReviewDetailResponse.newBuilder()
                .setId(r.getId())
                .setType(ProtoMappers.nvl(r.getType()))
                .setUrl(ProtoMappers.nvl(r.getUrl()))
                .setCaption(ProtoMappers.nvl(r.getCaption()))
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .build();
    }
}
