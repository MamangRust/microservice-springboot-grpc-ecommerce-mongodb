package com.sanedge.ecommerce.merchant_policy.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.protobuf.StringValue;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.merchant_policy.domain.requests.FindAllMerchantPolicies;
import com.sanedge.ecommerce.merchant_policy.service.MerchantPolicyQueryService;
import com.sanedge.ecommerce.pb.merchant.FindAllMerchantRequest;
import com.sanedge.ecommerce.pb.merchant_policy.ApiResponseMerchantPolicies;
import com.sanedge.ecommerce.pb.merchant_policy.ApiResponsePaginationMerchantPolicies;
import com.sanedge.ecommerce.pb.merchant_policy.ApiResponsePaginationMerchantPoliciesDeleteAt;
import com.sanedge.ecommerce.pb.merchant_policy.FindByIdMerchantPoliciesRequest;
import com.sanedge.ecommerce.pb.merchant_policy.MerchantPoliciesResponse;
import com.sanedge.ecommerce.pb.merchant_policy.MerchantPoliciesResponseDeleteAt;
import com.sanedge.ecommerce.pb.merchant_policy.MerchantPolicyQueryServiceGrpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MerchantPolicyQueryHandler extends MerchantPolicyQueryServiceGrpc.MerchantPolicyQueryServiceImplBase {

    private final MerchantPolicyQueryService merchantPolicyQueryService;

    @Override
    public void findAll(FindAllMerchantRequest request,
            StreamObserver<ApiResponsePaginationMerchantPolicies> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.merchant_policy.domain.responses.MerchantPoliciesResponse>> res = merchantPolicyQueryService
                    .findAll(toFindAll(request));

            ApiResponsePaginationMerchantPolicies.Builder builder = ApiResponsePaginationMerchantPolicies.newBuilder()
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
    public void findById(FindByIdMerchantPoliciesRequest request, StreamObserver<ApiResponseMerchantPolicies> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.merchant_policy.domain.responses.MerchantPoliciesResponse> res = merchantPolicyQueryService
                    .findById(request.getId());

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
    public void findByActive(FindAllMerchantRequest request,
            StreamObserver<ApiResponsePaginationMerchantPoliciesDeleteAt> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.merchant_policy.domain.responses.MerchantPoliciesResponseDeleteAt>> res = merchantPolicyQueryService
                    .findByActive(toFindAll(request));

            ApiResponsePaginationMerchantPoliciesDeleteAt.Builder builder = ApiResponsePaginationMerchantPoliciesDeleteAt
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
            StreamObserver<ApiResponsePaginationMerchantPoliciesDeleteAt> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.merchant_policy.domain.responses.MerchantPoliciesResponseDeleteAt>> res = merchantPolicyQueryService
                    .findByTrashed(toFindAll(request));

            ApiResponsePaginationMerchantPoliciesDeleteAt.Builder builder = ApiResponsePaginationMerchantPoliciesDeleteAt
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

    private FindAllMerchantPolicies toFindAll(FindAllMerchantRequest request) {
        FindAllMerchantPolicies req = new FindAllMerchantPolicies();
        req.setPage(request.getPage());
        req.setPageSize(request.getPageSize());
        req.setSearch(request.getSearch());
        return req;
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
