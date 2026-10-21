package com.sanedge.ecommerce.merchant.handler;

import org.springframework.stereotype.Service;

import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.pb.merchant.ApiResponseMerchant;
import com.sanedge.ecommerce.pb.merchant.ApiResponseMerchantAll;
import com.sanedge.ecommerce.pb.merchant.ApiResponseMerchantDelete;
import com.sanedge.ecommerce.pb.merchant.ApiResponseMerchantDeleteAt;
import com.sanedge.ecommerce.pb.merchant.CreateMerchantRequest;
import com.sanedge.ecommerce.pb.merchant.FindByIdMerchantRequest;
import com.sanedge.ecommerce.pb.merchant.MerchantCommandServiceGrpc;
import com.sanedge.ecommerce.pb.merchant.MerchantResponse;
import com.sanedge.ecommerce.pb.merchant.MerchantResponseDeleteAt;
import com.sanedge.ecommerce.pb.merchant.UpdateMerchantRequest;
import com.sanedge.ecommerce.pb.merchant.UpdateMerchantStatusRequest;
import com.sanedge.ecommerce.merchant.service.MerchantCommandService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MerchantCommandHandler extends MerchantCommandServiceGrpc.MerchantCommandServiceImplBase {

    private final MerchantCommandService merchantCommandService;

    @Override
    public void create(CreateMerchantRequest request, StreamObserver<ApiResponseMerchant> observer) {
        try {
            com.sanedge.ecommerce.merchant.domain.requests.CreateMerchantRequest req = new com.sanedge.ecommerce.merchant.domain.requests.CreateMerchantRequest();
            req.setUserId(request.getUserId());
            req.setName(request.getName());
            req.setDescription(request.getDescription());
            req.setAddress(request.getAddress());
            req.setContactEmail(request.getContactEmail());
            req.setContactPhone(request.getContactPhone());
            req.setStatus(request.getStatus());

            ApiResponse<com.sanedge.ecommerce.merchant.domain.responses.MerchantResponse> res = merchantCommandService
                    .createMerchant(req);

            observer.onNext(ApiResponseMerchant.newBuilder()
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
    public void update(UpdateMerchantRequest request, StreamObserver<ApiResponseMerchant> observer) {
        try {
            com.sanedge.ecommerce.merchant.domain.requests.UpdateMerchantRequest req = new com.sanedge.ecommerce.merchant.domain.requests.UpdateMerchantRequest();
            req.setMerchantId(request.getMerchantId());
            req.setUserId(request.getUserId());
            req.setName(request.getName());
            req.setDescription(request.getDescription());
            req.setAddress(request.getAddress());
            req.setContactEmail(request.getContactEmail());
            req.setContactPhone(request.getContactPhone());
            req.setStatus(request.getStatus());

            ApiResponse<com.sanedge.ecommerce.merchant.domain.responses.MerchantResponse> res = merchantCommandService
                    .updateMerchant(req);

            observer.onNext(ApiResponseMerchant.newBuilder()
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
    public void updateStatus(UpdateMerchantStatusRequest request, StreamObserver<ApiResponseMerchant> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.merchant.domain.responses.MerchantResponse> res = merchantCommandService
                    .updateMerchantStatus(request.getMerchantId(), request.getStatus());

            observer.onNext(ApiResponseMerchant.newBuilder()
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
    public void trashedMerchant(FindByIdMerchantRequest request,
            StreamObserver<ApiResponseMerchantDeleteAt> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.merchant.domain.responses.MerchantResponseDeleteAt> res = merchantCommandService
                    .trashedMerchant(request.getId());
            observer.onNext(ApiResponseMerchantDeleteAt.newBuilder()
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
    public void restoreMerchant(FindByIdMerchantRequest request, StreamObserver<ApiResponseMerchant> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.merchant.domain.responses.MerchantResponseDeleteAt> res = merchantCommandService
                    .restoreMerchant(request.getId());
            observer.onNext(ApiResponseMerchant.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setData(toProtoFromDeleteAt(res.getData()))
                    .build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteMerchantPermanent(FindByIdMerchantRequest request,
            StreamObserver<ApiResponseMerchantDelete> observer) {
        try {
            ApiResponse<Boolean> res = merchantCommandService.deleteMerchantPermanent(request.getId());
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
    public void restoreAllMerchant(Empty request, StreamObserver<ApiResponseMerchantAll> observer) {
        try {
            ApiResponse<Boolean> res = merchantCommandService.restoreAllMerchant();
            observer.onNext(toAll(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteAllMerchantPermanent(Empty request, StreamObserver<ApiResponseMerchantAll> observer) {
        try {
            ApiResponse<Boolean> res = merchantCommandService.deleteAllMerchantPermanent();
            observer.onNext(toAll(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private MerchantResponse toProto(com.sanedge.ecommerce.merchant.domain.responses.MerchantResponse m) {
        if (m == null) {
            return MerchantResponse.getDefaultInstance();
        }
        return MerchantResponse.newBuilder()
                .setId(m.getId() == null ? 0 : m.getId().intValue())
                .setUserId(m.getUserId() == null ? 0 : m.getUserId())
                .setName(ProtoMappers.nvl(m.getName()))
                .setDescription(ProtoMappers.nvl(m.getDescription()))
                .setAddress(ProtoMappers.nvl(m.getAddress()))
                .setContactEmail(ProtoMappers.nvl(m.getContactEmail()))
                .setContactPhone(ProtoMappers.nvl(m.getContactPhone()))
                .setStatus(ProtoMappers.nvl(m.getStatus()))
                .setCreatedAt(ProtoMappers.nvl(m.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(m.getUpdatedAt()))
                .build();
    }

    private MerchantResponse toProtoFromDeleteAt(
            com.sanedge.ecommerce.merchant.domain.responses.MerchantResponseDeleteAt m) {
        if (m == null) {
            return MerchantResponse.getDefaultInstance();
        }
        return MerchantResponse.newBuilder()
                .setId(m.getId() == null ? 0 : m.getId().intValue())
                .setUserId(m.getUserId() == null ? 0 : m.getUserId())
                .setName(ProtoMappers.nvl(m.getName()))
                .setDescription(ProtoMappers.nvl(m.getDescription()))
                .setAddress(ProtoMappers.nvl(m.getAddress()))
                .setContactEmail(ProtoMappers.nvl(m.getContactEmail()))
                .setContactPhone(ProtoMappers.nvl(m.getContactPhone()))
                .setStatus(ProtoMappers.nvl(m.getStatus()))
                .setCreatedAt(ProtoMappers.nvl(m.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(m.getUpdatedAt()))
                .build();
    }

    private MerchantResponseDeleteAt toProtoDeleteAt(
            com.sanedge.ecommerce.merchant.domain.responses.MerchantResponseDeleteAt m) {
        if (m == null) {
            return MerchantResponseDeleteAt.getDefaultInstance();
        }
        return MerchantResponseDeleteAt.newBuilder()
                .setId(m.getId() == null ? 0 : m.getId().intValue())
                .setUserId(m.getUserId() == null ? 0 : m.getUserId())
                .setName(ProtoMappers.nvl(m.getName()))
                .setDescription(ProtoMappers.nvl(m.getDescription()))
                .setAddress(ProtoMappers.nvl(m.getAddress()))
                .setContactEmail(ProtoMappers.nvl(m.getContactEmail()))
                .setContactPhone(ProtoMappers.nvl(m.getContactPhone()))
                .setStatus(ProtoMappers.nvl(m.getStatus()))
                .setCreatedAt(ProtoMappers.nvl(m.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(m.getUpdatedAt()))
                .setDeletedAt(StringValue.of(ProtoMappers.nvl(m.getDeletedAt())))
                .build();
    }

    private ApiResponseMerchantAll toAll(ApiResponse<Boolean> res) {
        return ApiResponseMerchantAll.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()))
                .build();
    }
}
