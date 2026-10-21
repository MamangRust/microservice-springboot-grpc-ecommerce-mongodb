package com.sanedge.ecommerce.shipping_address.handler;

import org.springframework.stereotype.Service;

import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.pb.shipping_address.ApiResponseShipping;
import com.sanedge.ecommerce.pb.shipping_address.ApiResponseShippingAll;
import com.sanedge.ecommerce.pb.shipping_address.ApiResponseShippingDelete;
import com.sanedge.ecommerce.pb.shipping_address.ApiResponseShippingDeleteAt;
import com.sanedge.ecommerce.pb.shipping_address.CreateShippingAddressRequest;
import com.sanedge.ecommerce.pb.shipping_address.FindByIdShippingRequest;
import com.sanedge.ecommerce.pb.shipping_address.ShippingCommandServiceGrpc;
import com.sanedge.ecommerce.pb.shipping_address.ShippingResponse;
import com.sanedge.ecommerce.pb.shipping_address.ShippingResponseDeleteAt;
import com.sanedge.ecommerce.pb.shipping_address.UpdateShippingAddressRequest;
import com.sanedge.ecommerce.shipping_address.service.ShippingAddressCommandService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShippingAddressCommandHandler extends ShippingCommandServiceGrpc.ShippingCommandServiceImplBase {

    private final ShippingAddressCommandService shippingAddressCommandService;

    @Override
    public void createShipping(CreateShippingAddressRequest request, StreamObserver<ApiResponseShipping> observer) {
        try {
            com.sanedge.ecommerce.shipping_address.domain.requests.CreateShippingAddressRequest req = new com.sanedge.ecommerce.shipping_address.domain.requests.CreateShippingAddressRequest();
            req.setOrderId(request.getOrderId());
            req.setAlamat(request.getAlamat());
            req.setProvinsi(request.getProvinsi());
            req.setKota(request.getKota());
            req.setCourier(request.getCourier());
            req.setShippingMethod(request.getShippingMethod());
            req.setShippingCost(request.getShippingCost());
            req.setNegara(request.getNegara());

            ApiResponse<com.sanedge.ecommerce.shipping_address.domain.responses.ShippingAddressResponse> res = shippingAddressCommandService
                    .create(req);

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
    public void updateShipping(UpdateShippingAddressRequest request, StreamObserver<ApiResponseShipping> observer) {
        try {
            com.sanedge.ecommerce.shipping_address.domain.requests.UpdateShippingAddressRequest req = new com.sanedge.ecommerce.shipping_address.domain.requests.UpdateShippingAddressRequest();
            req.setShippingId(request.getShippingId());
            req.setOrderId(request.getOrderId());
            req.setAlamat(request.getAlamat());
            req.setProvinsi(request.getProvinsi());
            req.setKota(request.getKota());
            req.setCourier(request.getCourier());
            req.setShippingMethod(request.getShippingMethod());
            req.setShippingCost(request.getShippingCost());
            req.setNegara(request.getNegara());

            ApiResponse<com.sanedge.ecommerce.shipping_address.domain.responses.ShippingAddressResponse> res = shippingAddressCommandService
                    .update(req);

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
    public void trashedShipping(FindByIdShippingRequest request, StreamObserver<ApiResponseShippingDeleteAt> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.shipping_address.domain.responses.ShippingAddressResponseDeleteAt> res = shippingAddressCommandService
                    .trash(request.getId());
            observer.onNext(toDeleteAt(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void restoreShipping(FindByIdShippingRequest request, StreamObserver<ApiResponseShippingDeleteAt> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.shipping_address.domain.responses.ShippingAddressResponseDeleteAt> res = shippingAddressCommandService
                    .restore(request.getId());
            observer.onNext(toDeleteAt(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteShippingPermanent(FindByIdShippingRequest request,
            StreamObserver<ApiResponseShippingDelete> observer) {
        try {
            ApiResponse<Boolean> res = shippingAddressCommandService.deletePermanently(request.getId());
            observer.onNext(toDelete(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void restoreAllShipping(Empty request, StreamObserver<ApiResponseShippingAll> observer) {
        try {
            ApiResponse<Boolean> res = shippingAddressCommandService.restoreAll();
            observer.onNext(toAll(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteShippingByOrderPermanent(FindByIdShippingRequest request,
            StreamObserver<ApiResponseShippingDelete> observer) {
        try {
            // The domain command service exposes only permanent-delete by id, so the
            // by-order variant is mapped onto the standard permanent delete.
            ApiResponse<Boolean> res = shippingAddressCommandService.deletePermanently(request.getId());
            observer.onNext(toDelete(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteAllShippingPermanent(Empty request, StreamObserver<ApiResponseShippingAll> observer) {
        try {
            ApiResponse<Boolean> res = shippingAddressCommandService.deleteAllPermanent();
            observer.onNext(toAll(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private ApiResponseShippingDeleteAt toDeleteAt(
            ApiResponse<com.sanedge.ecommerce.shipping_address.domain.responses.ShippingAddressResponseDeleteAt> res) {
        ShippingResponseDeleteAt data = ShippingResponseDeleteAt.newBuilder()
                .setId(res.getData().getId() == null ? 0 : res.getData().getId().intValue())
                .setOrderId(res.getData().getOrderId() == null ? 0 : res.getData().getOrderId())
                .setAlamat(ProtoMappers.nvl(res.getData().getAlamat()))
                .setProvinsi(ProtoMappers.nvl(res.getData().getProvinsi()))
                .setNegara(ProtoMappers.nvl(res.getData().getNegara()))
                .setKota(ProtoMappers.nvl(res.getData().getKota()))
                .setShippingMethod(ProtoMappers.nvl(res.getData().getShippingMethod()))
                .setShippingCost(res.getData().getShippingCost() == null ? 0 : res.getData().getShippingCost())
                .setCreatedAt(ProtoMappers.nvl(res.getData().getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(res.getData().getUpdatedAt()))
                .setDeletedAt(StringValue.of(ProtoMappers.nvl(res.getData().getDeletedAt())))
                .build();
        return ApiResponseShippingDeleteAt.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()))
                .setData(data)
                .build();
    }

    private ApiResponseShippingDelete toDelete(ApiResponse<Boolean> res) {
        return ApiResponseShippingDelete.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()))
                .build();
    }

    private ApiResponseShippingAll toAll(ApiResponse<Boolean> res) {
        return ApiResponseShippingAll.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()))
                .build();
    }

    private ShippingResponse toProto(
            com.sanedge.ecommerce.shipping_address.domain.responses.ShippingAddressResponse r) {
        if (r == null) {
            return ShippingResponse.getDefaultInstance();
        }
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
}
