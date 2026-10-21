package com.sanedge.ecommerce.merchant_business.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.protobuf.StringValue;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.merchant_business.domain.requests.FindAllMerchantRequest;
import com.sanedge.ecommerce.merchant_business.service.MerchantBusinessQueryService;
import com.sanedge.ecommerce.pb.merchant_business.ApiResponseMerchantBusiness;
import com.sanedge.ecommerce.pb.merchant_business.ApiResponsePaginationMerchantBusiness;
import com.sanedge.ecommerce.pb.merchant_business.ApiResponsePaginationMerchantBusinessDeleteAt;
import com.sanedge.ecommerce.pb.merchant_business.FindByIdMerchantBusinessRequest;
import com.sanedge.ecommerce.pb.merchant_business.MerchantBusinessQueryServiceGrpc;
import com.sanedge.ecommerce.pb.merchant_business.MerchantBusinessResponse;
import com.sanedge.ecommerce.pb.merchant_business.MerchantBusinessResponseDeleteAt;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MerchantBusinessQueryHandler
        extends MerchantBusinessQueryServiceGrpc.MerchantBusinessQueryServiceImplBase {

    private final MerchantBusinessQueryService merchantBusinessQueryService;

    @Override
    public void findAll(com.sanedge.ecommerce.pb.merchant.FindAllMerchantRequest request,
            StreamObserver<ApiResponsePaginationMerchantBusiness> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.merchant_business.domain.responses.MerchantBusinessResponse>> res = merchantBusinessQueryService
                    .findAll(toFindAll(request));

            ApiResponsePaginationMerchantBusiness.Builder builder = ApiResponsePaginationMerchantBusiness.newBuilder()
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
    public void findById(FindByIdMerchantBusinessRequest request,
            StreamObserver<ApiResponseMerchantBusiness> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.merchant_business.domain.responses.MerchantBusinessResponse> res = merchantBusinessQueryService
                    .findById(request.getId());

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
    public void findByActive(com.sanedge.ecommerce.pb.merchant.FindAllMerchantRequest request,
            StreamObserver<ApiResponsePaginationMerchantBusinessDeleteAt> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.merchant_business.domain.responses.MerchantBusinessResponseDeleteAt>> res = merchantBusinessQueryService
                    .findByActive(toFindAll(request));

            ApiResponsePaginationMerchantBusinessDeleteAt.Builder builder = ApiResponsePaginationMerchantBusinessDeleteAt
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
    public void findByTrashed(com.sanedge.ecommerce.pb.merchant.FindAllMerchantRequest request,
            StreamObserver<ApiResponsePaginationMerchantBusinessDeleteAt> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.merchant_business.domain.responses.MerchantBusinessResponseDeleteAt>> res = merchantBusinessQueryService
                    .findByTrashed(toFindAll(request));

            ApiResponsePaginationMerchantBusinessDeleteAt.Builder builder = ApiResponsePaginationMerchantBusinessDeleteAt
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

    private FindAllMerchantRequest toFindAll(com.sanedge.ecommerce.pb.merchant.FindAllMerchantRequest request) {
        FindAllMerchantRequest req = new FindAllMerchantRequest();
        req.setPage(request.getPage());
        req.setPageSize(request.getPageSize());
        req.setSearch(request.getSearch());
        return req;
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
