package com.sanedge.ecommerce.merchant_detail.handler;

import org.springframework.stereotype.Service;

import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.merchant_detail.service.MerchantDetailCommandService;
import com.sanedge.ecommerce.pb.merchant.ApiResponseMerchantAll;
import com.sanedge.ecommerce.pb.merchant.ApiResponseMerchantDelete;
import com.sanedge.ecommerce.pb.merchant_detail.ApiResponseMerchantDetail;
import com.sanedge.ecommerce.pb.merchant_detail.ApiResponseMerchantDetailDeleteAt;
import com.sanedge.ecommerce.pb.merchant_detail.CreateMerchantDetailRequest;
import com.sanedge.ecommerce.pb.merchant_detail.FindByIdMerchantDetailRequest;
import com.sanedge.ecommerce.pb.merchant_detail.MerchantDetailCommandServiceGrpc;
import com.sanedge.ecommerce.pb.merchant_detail.MerchantDetailResponse;
import com.sanedge.ecommerce.pb.merchant_detail.MerchantDetailResponseDeleteAt;
import com.sanedge.ecommerce.pb.merchant_detail.UpdateMerchantDetailRequest;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MerchantDetailCommandHandler extends MerchantDetailCommandServiceGrpc.MerchantDetailCommandServiceImplBase {

    private final MerchantDetailCommandService merchantDetailCommandService;

    @Override
    public void create(CreateMerchantDetailRequest request, StreamObserver<ApiResponseMerchantDetail> observer) {
        try {
            com.sanedge.ecommerce.merchant_detail.domain.requests.CreateMerchantDetailRequest req = new com.sanedge.ecommerce.merchant_detail.domain.requests.CreateMerchantDetailRequest();
            req.setMerchantId(request.getMerchantId());
            req.setDisplayName(request.getDisplayName());
            req.setCoverImageUrl(request.getCoverImageUrl());
            req.setLogoUrl(request.getLogoUrl());
            req.setShortDescription(request.getShortDescription());
            req.setWebsiteUrl(request.getWebsiteUrl());

            ApiResponse<com.sanedge.ecommerce.merchant_detail.domain.responses.MerchantDetailResponse> res = merchantDetailCommandService
                    .createMerchant(req);

            observer.onNext(ApiResponseMerchantDetail.newBuilder()
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
    public void update(UpdateMerchantDetailRequest request, StreamObserver<ApiResponseMerchantDetail> observer) {
        try {
            com.sanedge.ecommerce.merchant_detail.domain.requests.UpdateMerchantDetailRequest req = new com.sanedge.ecommerce.merchant_detail.domain.requests.UpdateMerchantDetailRequest();
            req.setMerchantDetailId(request.getMerchantDetailId());
            req.setDisplayName(request.getDisplayName());
            req.setCoverImageUrl(request.getCoverImageUrl());
            req.setLogoUrl(request.getLogoUrl());
            req.setShortDescription(request.getShortDescription());
            req.setWebsiteUrl(request.getWebsiteUrl());

            ApiResponse<com.sanedge.ecommerce.merchant_detail.domain.responses.MerchantDetailResponse> res = merchantDetailCommandService
                    .updateMerchant(req);

            observer.onNext(ApiResponseMerchantDetail.newBuilder()
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
    public void trashedMerchantDetail(FindByIdMerchantDetailRequest request,
            StreamObserver<ApiResponseMerchantDetailDeleteAt> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.merchant_detail.domain.responses.MerchantDetailResponseDeleteAt> res = merchantDetailCommandService
                    .trashedMerchant(request.getId());

            observer.onNext(ApiResponseMerchantDetailDeleteAt.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setData(toProtoDeleteAt(res.getData()))
                    .build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void restoreMerchantDetail(FindByIdMerchantDetailRequest request,
            StreamObserver<ApiResponseMerchantDetailDeleteAt> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.merchant_detail.domain.responses.MerchantDetailResponseDeleteAt> res = merchantDetailCommandService
                    .restoreMerchant(request.getId());

            observer.onNext(ApiResponseMerchantDetailDeleteAt.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setData(toProtoDeleteAt(res.getData()))
                    .build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteMerchantDetailPermanent(FindByIdMerchantDetailRequest request,
            StreamObserver<ApiResponseMerchantDelete> observer) {
        try {
            ApiResponse<Boolean> res = merchantDetailCommandService.deleteMerchantPermanent(request.getId());

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
    public void restoreAllMerchantDetail(Empty request, StreamObserver<ApiResponseMerchantAll> observer) {
        try {
            ApiResponse<Boolean> res = merchantDetailCommandService.restoreAllMerchant();

            observer.onNext(ApiResponseMerchantAll.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteAllMerchantDetailPermanent(Empty request, StreamObserver<ApiResponseMerchantAll> observer) {
        try {
            ApiResponse<Boolean> res = merchantDetailCommandService.deleteAllMerchantPermanent();

            observer.onNext(ApiResponseMerchantAll.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private MerchantDetailResponse toProto(
            com.sanedge.ecommerce.merchant_detail.domain.responses.MerchantDetailResponse r) {
        if (r == null) {
            return MerchantDetailResponse.getDefaultInstance();
        }
        return MerchantDetailResponse.newBuilder()
                .setId(r.getId() == null ? 0 : r.getId().intValue())
                .setMerchantId(r.getMerchantId() == null ? 0 : r.getMerchantId())
                .setDisplayName(ProtoMappers.nvl(r.getDisplayName()))
                .setCoverImageUrl(ProtoMappers.nvl(r.getCoverImageUrl()))
                .setLogoUrl(ProtoMappers.nvl(r.getLogoUrl()))
                .setShortDescription(ProtoMappers.nvl(r.getShortDescription()))
                .setWebsiteUrl(ProtoMappers.nvl(r.getWebsiteUrl()))
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .build();
    }

    private MerchantDetailResponseDeleteAt toProtoDeleteAt(
            com.sanedge.ecommerce.merchant_detail.domain.responses.MerchantDetailResponseDeleteAt r) {
        if (r == null) {
            return MerchantDetailResponseDeleteAt.getDefaultInstance();
        }
        return MerchantDetailResponseDeleteAt.newBuilder()
                .setId(r.getId() == null ? 0 : r.getId().intValue())
                .setMerchantId(r.getMerchantId() == null ? 0 : r.getMerchantId())
                .setDisplayName(ProtoMappers.nvl(r.getDisplayName()))
                .setCoverImageUrl(ProtoMappers.nvl(r.getCoverImageUrl()))
                .setLogoUrl(ProtoMappers.nvl(r.getLogoUrl()))
                .setShortDescription(ProtoMappers.nvl(r.getShortDescription()))
                .setWebsiteUrl(ProtoMappers.nvl(r.getWebsiteUrl()))
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .setDeletedAt(StringValue.of(ProtoMappers.nvl(r.getDeletedAt())))
                .build();
    }
}
