package com.sanedge.ecommerce.merchant_award.handler;

import org.springframework.stereotype.Service;

import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.merchant_award.service.MerchantAwardCommandService;
import com.sanedge.ecommerce.pb.merchant.ApiResponseMerchantAll;
import com.sanedge.ecommerce.pb.merchant.ApiResponseMerchantDelete;
import com.sanedge.ecommerce.pb.merchant_award.ApiResponseMerchantAward;
import com.sanedge.ecommerce.pb.merchant_award.ApiResponseMerchantAwardDeleteAt;
import com.sanedge.ecommerce.pb.merchant_award.CreateMerchantAwardRequest;
import com.sanedge.ecommerce.pb.merchant_award.FindByIdMerchantAwardRequest;
import com.sanedge.ecommerce.pb.merchant_award.MerchantAwardCommandServiceGrpc;
import com.sanedge.ecommerce.pb.merchant_award.MerchantAwardResponse;
import com.sanedge.ecommerce.pb.merchant_award.MerchantAwardResponseDeleteAt;
import com.sanedge.ecommerce.pb.merchant_award.UpdateMerchantAwardRequest;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MerchantAwardCommandHandler extends MerchantAwardCommandServiceGrpc.MerchantAwardCommandServiceImplBase {

    private final MerchantAwardCommandService merchantAwardCommandService;

    @Override
    public void create(CreateMerchantAwardRequest request, StreamObserver<ApiResponseMerchantAward> observer) {
        try {
            com.sanedge.ecommerce.merchant_award.domain.requests.CreateMerchantAwardRequest req = new com.sanedge.ecommerce.merchant_award.domain.requests.CreateMerchantAwardRequest();
            req.setMerchantId(request.getMerchantId());
            req.setTitle(request.getTitle());
            req.setDescription(request.getDescription());
            req.setIssuedBy(request.getIssuedBy());
            req.setIssueDate(request.getIssueDate());
            req.setExpiryDate(request.getExpiryDate());
            req.setCertificateUrl(request.getCertificateUrl());

            ApiResponse<com.sanedge.ecommerce.merchant_award.domain.responses.MerchantAwardResponse> res = merchantAwardCommandService
                    .createMerchantAward(req);

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
    public void update(UpdateMerchantAwardRequest request, StreamObserver<ApiResponseMerchantAward> observer) {
        try {
            com.sanedge.ecommerce.merchant_award.domain.requests.UpdateMerchantAwardRequest req = new com.sanedge.ecommerce.merchant_award.domain.requests.UpdateMerchantAwardRequest();
            req.setMerchantCertificationId(request.getMerchantCertificationId());
            req.setTitle(request.getTitle());
            req.setDescription(request.getDescription());
            req.setIssuedBy(request.getIssuedBy());
            req.setIssueDate(request.getIssueDate());
            req.setExpiryDate(request.getExpiryDate());
            req.setCertificateUrl(request.getCertificateUrl());

            ApiResponse<com.sanedge.ecommerce.merchant_award.domain.responses.MerchantAwardResponse> res = merchantAwardCommandService
                    .updateMerchantAward(req);

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
    public void trashedMerchantAward(FindByIdMerchantAwardRequest request,
            StreamObserver<ApiResponseMerchantAwardDeleteAt> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.merchant_award.domain.responses.MerchantAwardResponseDeleteAt> res = merchantAwardCommandService
                    .trashedMerchantAward(request.getId());
            observer.onNext(toDeleteAt(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void restoreMerchantAward(FindByIdMerchantAwardRequest request,
            StreamObserver<ApiResponseMerchantAwardDeleteAt> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.merchant_award.domain.responses.MerchantAwardResponseDeleteAt> res = merchantAwardCommandService
                    .restoreMerchantAward(request.getId());
            observer.onNext(toDeleteAt(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteMerchantAwardPermanent(FindByIdMerchantAwardRequest request,
            StreamObserver<ApiResponseMerchantDelete> observer) {
        try {
            ApiResponse<Boolean> res = merchantAwardCommandService.deleteMerchantAwardPermanent(request.getId());
            observer.onNext(ApiResponseMerchantDelete.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void restoreAllMerchantAward(Empty request, StreamObserver<ApiResponseMerchantAll> observer) {
        try {
            ApiResponse<Boolean> res = merchantAwardCommandService.restoreAllMerchantAward();
            observer.onNext(toAll(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteAllMerchantAwardPermanent(Empty request, StreamObserver<ApiResponseMerchantAll> observer) {
        try {
            ApiResponse<Boolean> res = merchantAwardCommandService.deleteAllMerchantAwardPermanent();
            observer.onNext(toAll(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private ApiResponseMerchantAwardDeleteAt toDeleteAt(
            ApiResponse<com.sanedge.ecommerce.merchant_award.domain.responses.MerchantAwardResponseDeleteAt> res) {
        MerchantAwardResponseDeleteAt data = MerchantAwardResponseDeleteAt.newBuilder()
                .setId(res.getData().getId() == null ? 0 : res.getData().getId().intValue())
                .setMerchantId(res.getData().getMerchantId() == null ? 0 : res.getData().getMerchantId())
                .setTitle(ProtoMappers.nvl(res.getData().getTitle()))
                .setDescription(ProtoMappers.nvl(res.getData().getDescription()))
                .setIssuedBy(ProtoMappers.nvl(res.getData().getIssuedBy()))
                .setIssueDate(ProtoMappers.nvl(res.getData().getIssueDate()))
                .setExpiryDate(ProtoMappers.nvl(res.getData().getExpiryDate()))
                .setCertificateUrl(ProtoMappers.nvl(res.getData().getCertificateUrl()))
                .setCreatedAt(ProtoMappers.nvl(res.getData().getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(res.getData().getUpdatedAt()))
                .setDeletedAt(StringValue.of(ProtoMappers.nvl(res.getData().getDeletedAt())))
                .build();
        return ApiResponseMerchantAwardDeleteAt.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()))
                .setData(data)
                .build();
    }

    private ApiResponseMerchantAll toAll(ApiResponse<Boolean> res) {
        return ApiResponseMerchantAll.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()))
                .build();
    }

    private MerchantAwardResponse toProto(
            com.sanedge.ecommerce.merchant_award.domain.responses.MerchantAwardResponse r) {
        if (r == null) {
            return MerchantAwardResponse.getDefaultInstance();
        }
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
}
