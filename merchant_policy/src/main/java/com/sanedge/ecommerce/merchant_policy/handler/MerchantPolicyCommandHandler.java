package com.sanedge.ecommerce.merchant_policy.handler;

import org.springframework.stereotype.Service;

import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.merchant_policy.service.MerchantPolicyCommandService;
import com.sanedge.ecommerce.pb.merchant.ApiResponseMerchantAll;
import com.sanedge.ecommerce.pb.merchant.ApiResponseMerchantDelete;
import com.sanedge.ecommerce.pb.merchant_policy.ApiResponseMerchantPolicies;
import com.sanedge.ecommerce.pb.merchant_policy.ApiResponseMerchantPoliciesDeleteAt;
import com.sanedge.ecommerce.pb.merchant_policy.CreateMerchantPoliciesRequest;
import com.sanedge.ecommerce.pb.merchant_policy.FindByIdMerchantPoliciesRequest;
import com.sanedge.ecommerce.pb.merchant_policy.MerchantPoliciesResponse;
import com.sanedge.ecommerce.pb.merchant_policy.MerchantPoliciesResponseDeleteAt;
import com.sanedge.ecommerce.pb.merchant_policy.MerchantPolicyCommandServiceGrpc;
import com.sanedge.ecommerce.pb.merchant_policy.UpdateMerchantPoliciesRequest;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MerchantPolicyCommandHandler extends MerchantPolicyCommandServiceGrpc.MerchantPolicyCommandServiceImplBase {

    private final MerchantPolicyCommandService merchantPolicyCommandService;

    @Override
    public void create(CreateMerchantPoliciesRequest request, StreamObserver<ApiResponseMerchantPolicies> observer) {
        try {
            com.sanedge.ecommerce.merchant_policy.domain.requests.CreateMerchantPolicyRequest req = new com.sanedge.ecommerce.merchant_policy.domain.requests.CreateMerchantPolicyRequest();
            req.setMerchantId(request.getMerchantId());
            req.setPolicyType(request.getPolicyType());
            req.setTitle(request.getTitle());
            req.setDescription(request.getDescription());

            ApiResponse<com.sanedge.ecommerce.merchant_policy.domain.responses.MerchantPoliciesResponse> res = merchantPolicyCommandService
                    .create(req);

            ApiResponseMerchantPolicies.Builder builder = ApiResponseMerchantPolicies.newBuilder()
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
    public void update(UpdateMerchantPoliciesRequest request, StreamObserver<ApiResponseMerchantPolicies> observer) {
        try {
            com.sanedge.ecommerce.merchant_policy.domain.requests.UpdateMerchantPolicyRequest req = new com.sanedge.ecommerce.merchant_policy.domain.requests.UpdateMerchantPolicyRequest();
            req.setMerchantPolicyId(request.getMerchantPolicyId());
            req.setPolicyType(request.getPolicyType());
            req.setTitle(request.getTitle());
            req.setDescription(request.getDescription());

            ApiResponse<com.sanedge.ecommerce.merchant_policy.domain.responses.MerchantPoliciesResponse> res = merchantPolicyCommandService
                    .update(req);

            ApiResponseMerchantPolicies.Builder builder = ApiResponseMerchantPolicies.newBuilder()
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
    public void trashedMerchantPolicies(FindByIdMerchantPoliciesRequest request,
            StreamObserver<ApiResponseMerchantPoliciesDeleteAt> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.merchant_policy.domain.responses.MerchantPoliciesResponseDeleteAt> res = merchantPolicyCommandService
                    .trash(request.getId());
            observer.onNext(toDeleteAt(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void restoreMerchantPolicies(FindByIdMerchantPoliciesRequest request,
            StreamObserver<ApiResponseMerchantPoliciesDeleteAt> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.merchant_policy.domain.responses.MerchantPoliciesResponseDeleteAt> res = merchantPolicyCommandService
                    .restore(request.getId());
            observer.onNext(toDeleteAt(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteMerchantPoliciesPermanent(FindByIdMerchantPoliciesRequest request,
            StreamObserver<ApiResponseMerchantDelete> observer) {
        try {
            ApiResponse<Boolean> res = merchantPolicyCommandService.delete(request.getId());
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
    public void restoreAllMerchantPolicies(Empty request, StreamObserver<ApiResponseMerchantAll> observer) {
        try {
            ApiResponse<Boolean> res = merchantPolicyCommandService.restoreAll();
            observer.onNext(toAll(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteAllMerchantPoliciesPermanent(Empty request, StreamObserver<ApiResponseMerchantAll> observer) {
        try {
            ApiResponse<Boolean> res = merchantPolicyCommandService.deleteAll();
            observer.onNext(toAll(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private ApiResponseMerchantPoliciesDeleteAt toDeleteAt(
            ApiResponse<com.sanedge.ecommerce.merchant_policy.domain.responses.MerchantPoliciesResponseDeleteAt> res) {
        ApiResponseMerchantPoliciesDeleteAt.Builder builder = ApiResponseMerchantPoliciesDeleteAt.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()));
        if (res.getData() != null) {
            builder.setData(toProtoDeleteAt(res.getData()));
        }
        return builder.build();
    }

    private ApiResponseMerchantAll toAll(ApiResponse<Boolean> res) {
        return ApiResponseMerchantAll.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()))
                .build();
    }

    private MerchantPoliciesResponse toProto(
            com.sanedge.ecommerce.merchant_policy.domain.responses.MerchantPoliciesResponse r) {
        return MerchantPoliciesResponse.newBuilder()
                .setId(r.getId() == null ? 0 : r.getId().intValue())
                .setMerchantId(r.getMerchantId() == null ? 0 : r.getMerchantId())
                .setPolicyType(ProtoMappers.nvl(r.getPolicyType()))
                .setTitle(ProtoMappers.nvl(r.getTitle()))
                .setDescription(ProtoMappers.nvl(r.getDescription()))
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .build();
    }

    private MerchantPoliciesResponseDeleteAt toProtoDeleteAt(
            com.sanedge.ecommerce.merchant_policy.domain.responses.MerchantPoliciesResponseDeleteAt r) {
        return MerchantPoliciesResponseDeleteAt.newBuilder()
                .setId(r.getId() == null ? 0 : r.getId().intValue())
                .setMerchantId(r.getMerchantId() == null ? 0 : r.getMerchantId())
                .setPolicyType(ProtoMappers.nvl(r.getPolicyType()))
                .setTitle(ProtoMappers.nvl(r.getTitle()))
                .setDescription(ProtoMappers.nvl(r.getDescription()))
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .setDeletedAt(StringValue.of(ProtoMappers.nvl(r.getDeletedAt())))
                .build();
    }
}
