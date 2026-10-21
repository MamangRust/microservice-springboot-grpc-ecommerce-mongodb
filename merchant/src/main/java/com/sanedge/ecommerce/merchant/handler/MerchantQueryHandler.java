package com.sanedge.ecommerce.merchant.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.protobuf.StringValue;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.merchant.service.MerchantQueryService;
import com.sanedge.ecommerce.pb.merchant.ApiResponseMerchant;
import com.sanedge.ecommerce.pb.merchant.ApiResponsePaginationMerchant;
import com.sanedge.ecommerce.pb.merchant.ApiResponsePaginationMerchantDeleteAt;
import com.sanedge.ecommerce.pb.merchant.FindAllMerchantRequest;
import com.sanedge.ecommerce.pb.merchant.FindByIdMerchantRequest;
import com.sanedge.ecommerce.pb.merchant.MerchantQueryServiceGrpc;
import com.sanedge.ecommerce.pb.merchant.MerchantResponse;
import com.sanedge.ecommerce.pb.merchant.MerchantResponseDeleteAt;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MerchantQueryHandler extends MerchantQueryServiceGrpc.MerchantQueryServiceImplBase {

    private final MerchantQueryService merchantQueryService;

    @Override
    public void findAll(FindAllMerchantRequest request, StreamObserver<ApiResponsePaginationMerchant> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.merchant.domain.responses.MerchantResponse>> res = merchantQueryService
                    .findAll(toFindAll(request));

            ApiResponsePaginationMerchant.Builder builder = ApiResponsePaginationMerchant.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setPagination(ProtoMappers.toProtoPagination(res.getPagination()));
            res.getData().forEach(m -> builder.addData(toProto(m)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findById(FindByIdMerchantRequest request, StreamObserver<ApiResponseMerchant> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.merchant.domain.responses.MerchantResponse> res = merchantQueryService
                    .findById(request.getId());

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
    public void findByActive(FindAllMerchantRequest request,
            StreamObserver<ApiResponsePaginationMerchantDeleteAt> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.merchant.domain.responses.MerchantResponseDeleteAt>> res = merchantQueryService
                    .findByActive(toFindAll(request));

            ApiResponsePaginationMerchantDeleteAt.Builder builder = ApiResponsePaginationMerchantDeleteAt.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setPagination(ProtoMappers.toProtoPagination(res.getPagination()));
            res.getData().forEach(m -> builder.addData(toProtoDeleteAt(m)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findByTrashed(FindAllMerchantRequest request,
            StreamObserver<ApiResponsePaginationMerchantDeleteAt> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.merchant.domain.responses.MerchantResponseDeleteAt>> res = merchantQueryService
                    .findByTrashed(toFindAll(request));

            ApiResponsePaginationMerchantDeleteAt.Builder builder = ApiResponsePaginationMerchantDeleteAt.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setPagination(ProtoMappers.toProtoPagination(res.getPagination()));
            res.getData().forEach(m -> builder.addData(toProtoDeleteAt(m)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private com.sanedge.ecommerce.merchant.domain.requests.FindAllMerchants toFindAll(
            FindAllMerchantRequest request) {
        com.sanedge.ecommerce.merchant.domain.requests.FindAllMerchants req = new com.sanedge.ecommerce.merchant.domain.requests.FindAllMerchants();
        req.setPage(request.getPage());
        req.setPageSize(request.getPageSize());
        req.setSearch(request.getSearch());
        return req;
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
}
