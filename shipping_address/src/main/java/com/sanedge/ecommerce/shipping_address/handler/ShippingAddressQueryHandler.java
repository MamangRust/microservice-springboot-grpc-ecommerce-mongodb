package com.sanedge.ecommerce.shipping_address.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.protobuf.StringValue;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.pb.shipping_address.ApiResponsePaginationShipping;
import com.sanedge.ecommerce.pb.shipping_address.ApiResponsePaginationShippingDeleteAt;
import com.sanedge.ecommerce.pb.shipping_address.ApiResponseShipping;
import com.sanedge.ecommerce.pb.shipping_address.FindAllShippingRequest;
import com.sanedge.ecommerce.pb.shipping_address.FindByIdShippingRequest;
import com.sanedge.ecommerce.pb.shipping_address.ShippingQueryServiceGrpc;
import com.sanedge.ecommerce.pb.shipping_address.ShippingResponse;
import com.sanedge.ecommerce.pb.shipping_address.ShippingResponseDeleteAt;
import com.sanedge.ecommerce.shipping_address.domain.requests.FindAllShippingAddress;
import com.sanedge.ecommerce.shipping_address.service.ShippingAddressQueryService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShippingAddressQueryHandler extends ShippingQueryServiceGrpc.ShippingQueryServiceImplBase {

    private final ShippingAddressQueryService shippingAddressQueryService;

    @Override
    public void findAll(FindAllShippingRequest request, StreamObserver<ApiResponsePaginationShipping> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.shipping_address.domain.responses.ShippingAddressResponse>> res = shippingAddressQueryService
                    .findAll(toFindAll(request));

            ApiResponsePaginationShipping.Builder builder = ApiResponsePaginationShipping.newBuilder()
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
    public void findById(FindByIdShippingRequest request, StreamObserver<ApiResponseShipping> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.shipping_address.domain.responses.ShippingAddressResponse> res = shippingAddressQueryService
                    .findById(request.getId());

            observer.onNext(ApiResponseShipping.newBuilder()
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
    public void findByOrder(FindByIdShippingRequest request, StreamObserver<ApiResponseShipping> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.shipping_address.domain.responses.ShippingAddressResponse> res = shippingAddressQueryService
                    .findByOrder(request.getId());

            observer.onNext(ApiResponseShipping.newBuilder()
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
    public void findByActive(FindAllShippingRequest request,
            StreamObserver<ApiResponsePaginationShippingDeleteAt> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.shipping_address.domain.responses.ShippingAddressResponseDeleteAt>> res = shippingAddressQueryService
                    .findByActive(toFindAll(request));

            ApiResponsePaginationShippingDeleteAt.Builder builder = ApiResponsePaginationShippingDeleteAt.newBuilder()
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
    public void findByTrashed(FindAllShippingRequest request,
            StreamObserver<ApiResponsePaginationShippingDeleteAt> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.shipping_address.domain.responses.ShippingAddressResponseDeleteAt>> res = shippingAddressQueryService
                    .findByTrashed(toFindAll(request));

            ApiResponsePaginationShippingDeleteAt.Builder builder = ApiResponsePaginationShippingDeleteAt.newBuilder()
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

    private FindAllShippingAddress toFindAll(FindAllShippingRequest request) {
        FindAllShippingAddress req = new FindAllShippingAddress();
        req.setPage(request.getPage());
        req.setPageSize(request.getPageSize());
        req.setSearch(request.getSearch());
        return req;
    }

    private ShippingResponse toProto(com.sanedge.ecommerce.shipping_address.domain.responses.ShippingAddressResponse r) {
        return ShippingResponse.newBuilder()
                .setId(r.getId() == null ? 0 : r.getId().intValue())
                .setOrderId(r.getOrderId() == null ? 0 : r.getOrderId())
                .setAlamat(ProtoMappers.nvl(r.getAlamat()))
                .setProvinsi(ProtoMappers.nvl(r.getProvinsi()))
                .setNegara(ProtoMappers.nvl(r.getNegara()))
                .setKota(ProtoMappers.nvl(r.getKota()))
                .setShippingMethod(ProtoMappers.nvl(r.getShippingMethod()))
                .setShippingCost(r.getShippingCost() == null ? 0 : r.getShippingCost())
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .build();
    }

    private ShippingResponseDeleteAt toProtoDeleteAt(
            com.sanedge.ecommerce.shipping_address.domain.responses.ShippingAddressResponseDeleteAt r) {
        return ShippingResponseDeleteAt.newBuilder()
                .setId(r.getId() == null ? 0 : r.getId().intValue())
                .setOrderId(r.getOrderId() == null ? 0 : r.getOrderId())
                .setAlamat(ProtoMappers.nvl(r.getAlamat()))
                .setProvinsi(ProtoMappers.nvl(r.getProvinsi()))
                .setNegara(ProtoMappers.nvl(r.getNegara()))
                .setKota(ProtoMappers.nvl(r.getKota()))
                .setShippingMethod(ProtoMappers.nvl(r.getShippingMethod()))
                .setShippingCost(r.getShippingCost() == null ? 0 : r.getShippingCost())
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .setDeletedAt(StringValue.of(ProtoMappers.nvl(r.getDeletedAt())))
                .build();
    }
}
