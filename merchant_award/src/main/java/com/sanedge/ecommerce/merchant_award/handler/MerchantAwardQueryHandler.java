package com.sanedge.ecommerce.merchant_award.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.merchant_award.domain.requests.FindAllMerchantAwards;
import com.sanedge.ecommerce.merchant_award.service.MerchantAwardQueryService;
import com.sanedge.ecommerce.pb.merchant.FindAllMerchantRequest;
import com.sanedge.ecommerce.pb.merchant_award.ApiResponseMerchantAward;
import com.sanedge.ecommerce.pb.merchant_award.ApiResponsePaginationMerchantAward;
import com.sanedge.ecommerce.pb.merchant_award.ApiResponsePaginationMerchantAwardDeleteAt;
import com.sanedge.ecommerce.pb.merchant_award.FindByIdMerchantAwardRequest;
import com.sanedge.ecommerce.pb.merchant_award.MerchantAwardQueryServiceGrpc;
import com.sanedge.ecommerce.pb.merchant_award.MerchantAwardResponse;
import com.sanedge.ecommerce.pb.merchant_award.MerchantAwardResponseDeleteAt;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MerchantAwardQueryHandler extends MerchantAwardQueryServiceGrpc.MerchantAwardQueryServiceImplBase {

    private final MerchantAwardQueryService merchantAwardQueryService;

    @Override
    public void findAll(FindAllMerchantRequest request, StreamObserver<ApiResponsePaginationMerchantAward> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.merchant_award.domain.responses.MerchantAwardResponse>> res = merchantAwardQueryService
                    .findAll(toFindAll(request));

            ApiResponsePaginationMerchantAward.Builder builder = ApiResponsePaginationMerchantAward.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setPagination(ProtoMappers.toProtoPagination(res.getPagination()));
            res.getData().forEach(r -> builder.addData(toProto(r)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findById(FindByIdMerchantAwardRequest request, StreamObserver<ApiResponseMerchantAward> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.merchant_award.domain.responses.MerchantAwardResponse> res = merchantAwardQueryService
                    .findById(request.getId());

            observer.onNext(ApiResponseMerchantAward.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setData(toProto(res.getData()))
                    .build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findByActive(FindAllMerchantRequest request,
            StreamObserver<ApiResponsePaginationMerchantAwardDeleteAt> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.merchant_award.domain.responses.MerchantAwardResponseDeleteAt>> res = merchantAwardQueryService
                    .findByActive(toFindAll(request));

            ApiResponsePaginationMerchantAwardDeleteAt.Builder builder = ApiResponsePaginationMerchantAwardDeleteAt
                    .newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setPagination(ProtoMappers.toProtoPagination(res.getPagination()));
            res.getData().forEach(r -> builder.addData(toProtoDeleteAt(r)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findByTrashed(FindAllMerchantRequest request,
            StreamObserver<ApiResponsePaginationMerchantAwardDeleteAt> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.merchant_award.domain.responses.MerchantAwardResponseDeleteAt>> res = merchantAwardQueryService
                    .findByTrashed(toFindAll(request));

            ApiResponsePaginationMerchantAwardDeleteAt.Builder builder = ApiResponsePaginationMerchantAwardDeleteAt
                    .newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setPagination(ProtoMappers.toProtoPagination(res.getPagination()));
            res.getData().forEach(r -> builder.addData(toProtoDeleteAt(r)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private FindAllMerchantAwards toFindAll(FindAllMerchantRequest request) {
        FindAllMerchantAwards req = new FindAllMerchantAwards();
        req.setPage(request.getPage());
        req.setPageSize(request.getPageSize());
        req.setSearch(request.getSearch());
        return req;
    }

    private MerchantAwardResponse toProto(
            com.sanedge.ecommerce.merchant_award.domain.responses.MerchantAwardResponse r) {
        return MerchantAwardResponse.newBuilder()
                .setId(r.getId() == null ? 0 : r.getId().intValue())
                .setMerchantId(r.getMerchantId() == null ? 0 : r.getMerchantId())
                .setTitle(ProtoMappers.nvl(r.getTitle()))
                .setDescription(ProtoMappers.nvl(r.getDescription()))
                .setIssuedBy(ProtoMappers.nvl(r.getIssuedBy()))
                .setIssueDate(ProtoMappers.nvl(r.getIssueDate()))
                .setExpiryDate(ProtoMappers.nvl(r.getExpiryDate()))
                .setCertificateUrl(ProtoMappers.nvl(r.getCertificateUrl()))
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .build();
    }

    private MerchantAwardResponseDeleteAt toProtoDeleteAt(
            com.sanedge.ecommerce.merchant_award.domain.responses.MerchantAwardResponseDeleteAt r) {
        return MerchantAwardResponseDeleteAt.newBuilder()
                .setId(r.getId() == null ? 0 : r.getId().intValue())
                .setMerchantId(r.getMerchantId() == null ? 0 : r.getMerchantId())
                .setTitle(ProtoMappers.nvl(r.getTitle()))
                .setDescription(ProtoMappers.nvl(r.getDescription()))
                .setIssuedBy(ProtoMappers.nvl(r.getIssuedBy()))
                .setIssueDate(ProtoMappers.nvl(r.getIssueDate()))
                .setExpiryDate(ProtoMappers.nvl(r.getExpiryDate()))
                .setCertificateUrl(ProtoMappers.nvl(r.getCertificateUrl()))
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .setDeletedAt(com.google.protobuf.StringValue.of(ProtoMappers.nvl(r.getDeletedAt())))
                .build();
    }
}
