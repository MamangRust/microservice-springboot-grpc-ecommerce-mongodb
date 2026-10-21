package com.sanedge.ecommerce.merchant_business.handler;

import org.springframework.stereotype.Service;

import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.merchant_business.service.MerchantBusinessCommandService;
import com.sanedge.ecommerce.pb.merchant.ApiResponseMerchantAll;
import com.sanedge.ecommerce.pb.merchant.ApiResponseMerchantDelete;
import com.sanedge.ecommerce.pb.merchant_business.ApiResponseMerchantBusiness;
import com.sanedge.ecommerce.pb.merchant_business.ApiResponseMerchantBusinessDeleteAt;
import com.sanedge.ecommerce.pb.merchant_business.CreateMerchantBusinessRequest;
import com.sanedge.ecommerce.pb.merchant_business.FindByIdMerchantBusinessRequest;
import com.sanedge.ecommerce.pb.merchant_business.MerchantBusinessCommandServiceGrpc;
import com.sanedge.ecommerce.pb.merchant_business.MerchantBusinessResponse;
import com.sanedge.ecommerce.pb.merchant_business.MerchantBusinessResponseDeleteAt;
import com.sanedge.ecommerce.pb.merchant_business.UpdateMerchantBusinessRequest;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MerchantBusinessCommandHandler
        extends MerchantBusinessCommandServiceGrpc.MerchantBusinessCommandServiceImplBase {

    private final MerchantBusinessCommandService merchantBusinessCommandService;

    @Override
    public void create(CreateMerchantBusinessRequest request, StreamObserver<ApiResponseMerchantBusiness> observer) {
        try {
            com.sanedge.ecommerce.merchant_business.domain.requests.CreateMerchantBusinessRequest req = new com.sanedge.ecommerce.merchant_business.domain.requests.CreateMerchantBusinessRequest();
            req.setMerchantId(request.getMerchantId());
            req.setBusinessType(request.getBusinessType());
            req.setTaxId(request.getTaxId());
            req.setEstablishedYear(request.getEstablishedYear());
            req.setNumberOfEmployees(request.getNumberOfEmployees());
            req.setWebsiteUrl(request.getWebsiteUrl());

            ApiResponse<com.sanedge.ecommerce.merchant_business.domain.responses.MerchantBusinessResponse> res = merchantBusinessCommandService
                    .createMerchantBusiness(req);

            observer.onNext(ApiResponseMerchantBusiness.newBuilder()
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
    public void update(UpdateMerchantBusinessRequest request, StreamObserver<ApiResponseMerchantBusiness> observer) {
        try {
            com.sanedge.ecommerce.merchant_business.domain.requests.UpdateMerchantBusinessRequest req = new com.sanedge.ecommerce.merchant_business.domain.requests.UpdateMerchantBusinessRequest();
            req.setMerchantBusinessInfoId(request.getMerchantBusinessInfoId());
            req.setBusinessType(request.getBusinessType());
            req.setTaxId(request.getTaxId());
            req.setEstablishedYear(request.getEstablishedYear());
            req.setNumberOfEmployees(request.getNumberOfEmployees());
            req.setWebsiteUrl(request.getWebsiteUrl());

            ApiResponse<com.sanedge.ecommerce.merchant_business.domain.responses.MerchantBusinessResponse> res = merchantBusinessCommandService
                    .updateMerchantBusiness(req);

            observer.onNext(ApiResponseMerchantBusiness.newBuilder()
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
    public void trashedMerchantBusiness(FindByIdMerchantBusinessRequest request,
            StreamObserver<ApiResponseMerchantBusinessDeleteAt> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.merchant_business.domain.responses.MerchantBusinessResponseDeleteAt> res = merchantBusinessCommandService
                    .trashedMerchantBusiness(request.getId());
            observer.onNext(toDeleteAt(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void restoreMerchantBusiness(FindByIdMerchantBusinessRequest request,
            StreamObserver<ApiResponseMerchantBusinessDeleteAt> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.merchant_business.domain.responses.MerchantBusinessResponseDeleteAt> res = merchantBusinessCommandService
                    .restoreMerchantBusiness(request.getId());
            observer.onNext(toDeleteAt(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteMerchantBusinessPermanent(FindByIdMerchantBusinessRequest request,
            StreamObserver<ApiResponseMerchantDelete> observer) {
        try {
            ApiResponse<Boolean> res = merchantBusinessCommandService
                    .deleteMerchantBusinessPermanent(request.getId());
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
    public void restoreAllMerchantBusiness(Empty request, StreamObserver<ApiResponseMerchantAll> observer) {
        try {
            ApiResponse<Boolean> res = merchantBusinessCommandService.restoreAllMerchantBusiness();
            observer.onNext(toAll(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteAllMerchantBusinessPermanent(Empty request, StreamObserver<ApiResponseMerchantAll> observer) {
        try {
            ApiResponse<Boolean> res = merchantBusinessCommandService.deleteAllMerchantBusinessPermanent();
            observer.onNext(toAll(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private ApiResponseMerchantBusinessDeleteAt toDeleteAt(
            ApiResponse<com.sanedge.ecommerce.merchant_business.domain.responses.MerchantBusinessResponseDeleteAt> res) {
        MerchantBusinessResponseDeleteAt data = toProtoDeleteAt(res.getData());
        return ApiResponseMerchantBusinessDeleteAt.newBuilder()
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

    private MerchantBusinessResponse toProto(
            com.sanedge.ecommerce.merchant_business.domain.responses.MerchantBusinessResponse r) {
        if (r == null) {
            return MerchantBusinessResponse.getDefaultInstance();
        }
        return MerchantBusinessResponse.newBuilder()
                .setId(r.getId() == null ? 0 : r.getId().intValue())
                .setMerchantId(r.getMerchantId() == null ? 0 : r.getMerchantId())
                .setBusinessType(ProtoMappers.nvl(r.getBusinessType()))
                .setTaxId(ProtoMappers.nvl(r.getTaxId()))
                .setEstablishedYear(r.getEstablishedYear() == null ? 0 : r.getEstablishedYear())
                .setNumberOfEmployees(r.getNumberOfEmployees() == null ? 0 : r.getNumberOfEmployees())
                .setWebsiteUrl(ProtoMappers.nvl(r.getWebsiteUrl()))
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .build();
    }

    private MerchantBusinessResponseDeleteAt toProtoDeleteAt(
            com.sanedge.ecommerce.merchant_business.domain.responses.MerchantBusinessResponseDeleteAt r) {
        if (r == null) {
            return MerchantBusinessResponseDeleteAt.getDefaultInstance();
        }
        return MerchantBusinessResponseDeleteAt.newBuilder()
                .setId(r.getId() == null ? 0 : r.getId().intValue())
                .setMerchantId(r.getMerchantId() == null ? 0 : r.getMerchantId())
                .setBusinessType(ProtoMappers.nvl(r.getBusinessType()))
                .setTaxId(ProtoMappers.nvl(r.getTaxId()))
                .setEstablishedYear(r.getEstablishedYear() == null ? 0 : r.getEstablishedYear())
                .setNumberOfEmployees(r.getNumberOfEmployees() == null ? 0 : r.getNumberOfEmployees())
                .setWebsiteUrl(ProtoMappers.nvl(r.getWebsiteUrl()))
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .setDeletedAt(StringValue.of(ProtoMappers.nvl(r.getDeletedAt())))
                .build();
    }
}
