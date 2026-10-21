package com.sanedge.ecommerce.order.handler;

import org.springframework.stereotype.Service;

import com.google.protobuf.Empty;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.order.domain.requests.shipping.CreateShippingAddressRequest;
import com.sanedge.ecommerce.order.domain.requests.shipping.UpdateShippingAddressRequest;
import com.sanedge.ecommerce.order.service.OrderCommandService;
import com.sanedge.ecommerce.pb.order.ApiResponseOrder;
import com.sanedge.ecommerce.pb.order.ApiResponseOrderAll;
import com.sanedge.ecommerce.pb.order.ApiResponseOrderDelete;
import com.sanedge.ecommerce.pb.order.ApiResponseOrderDeleteAt;
import com.sanedge.ecommerce.pb.order.CreateOrderRequest;
import com.sanedge.ecommerce.pb.order.FindByIdOrderRequest;
import com.sanedge.ecommerce.pb.order.OrderCommandServiceGrpc;
import com.sanedge.ecommerce.pb.order.OrderResponse;
import com.sanedge.ecommerce.pb.order.OrderResponseDeleteAt;
import com.sanedge.ecommerce.pb.order.UpdateOrderRequest;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderCommandHandler extends OrderCommandServiceGrpc.OrderCommandServiceImplBase {

    private final OrderCommandService orderCommandService;

    @Override
    public void create(CreateOrderRequest request, StreamObserver<ApiResponseOrder> observer) {
        try {
            com.sanedge.ecommerce.order.domain.requests.order.CreateOrderRequest req = new com.sanedge.ecommerce.order.domain.requests.order.CreateOrderRequest();
            req.setMerchantId(request.getMerchantId());
            req.setUserId(request.getUserId());
            req.setItems(request.getItemsList().stream().map(i -> {
                com.sanedge.ecommerce.order.domain.requests.order.CreateOrderItemRequest item = new com.sanedge.ecommerce.order.domain.requests.order.CreateOrderItemRequest();
                item.setProductId(i.getProductId());
                item.setQuantity(i.getQuantity());
                item.setPrice(i.getPrice());
                return item;
            }).toList());
            req.setShippingAddress(toCreateShipping(request.getShipping()));

            ApiResponse<com.sanedge.ecommerce.order.domain.responses.order.OrderResponse> res = orderCommandService
                    .create(req);

            observer.onNext(ApiResponseOrder.newBuilder()
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
    public void update(UpdateOrderRequest request, StreamObserver<ApiResponseOrder> observer) {
        try {
            com.sanedge.ecommerce.order.domain.requests.order.UpdateOrderRequest req = new com.sanedge.ecommerce.order.domain.requests.order.UpdateOrderRequest();
            req.setOrderId(request.getOrderId());
            req.setUserId(request.getUserId());
            req.setItems(request.getItemsList().stream().map(i -> {
                com.sanedge.ecommerce.order.domain.requests.order.UpdateOrderItemRequest item = new com.sanedge.ecommerce.order.domain.requests.order.UpdateOrderItemRequest();
                item.setOrderItemId(i.getOrderItemId());
                item.setProductId(i.getProductId());
                item.setQuantity(i.getQuantity());
                item.setPrice(i.getPrice());
                return item;
            }).toList());
            req.setShippingAddress(toUpdateShipping(request.getShipping()));

            ApiResponse<com.sanedge.ecommerce.order.domain.responses.order.OrderResponse> res = orderCommandService
                    .update(req);

            observer.onNext(ApiResponseOrder.newBuilder()
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
    public void trashedOrder(FindByIdOrderRequest request, StreamObserver<ApiResponseOrderDeleteAt> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.order.domain.responses.order.OrderResponseDeleteAt> res = orderCommandService
                    .trash(request.getId());
            observer.onNext(toDeleteAt(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void restoreOrder(FindByIdOrderRequest request, StreamObserver<ApiResponseOrderDeleteAt> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.order.domain.responses.order.OrderResponseDeleteAt> res = orderCommandService
                    .restore(request.getId());
            observer.onNext(toDeleteAt(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteOrderPermanent(FindByIdOrderRequest request, StreamObserver<ApiResponseOrderDelete> observer) {
        try {
            ApiResponse<Boolean> res = orderCommandService.delete(request.getId());
            observer.onNext(ApiResponseOrderDelete.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void restoreAllOrder(Empty request, StreamObserver<ApiResponseOrderAll> observer) {
        try {
            ApiResponse<Boolean> res = orderCommandService.restoreAll();
            observer.onNext(toAll(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteAllOrderPermanent(Empty request, StreamObserver<ApiResponseOrderAll> observer) {
        try {
            ApiResponse<Boolean> res = orderCommandService.deleteAll();
            observer.onNext(toAll(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private CreateShippingAddressRequest toCreateShipping(
            com.sanedge.ecommerce.pb.shipping_address.CreateShippingAddressRequest proto) {
        CreateShippingAddressRequest req = new CreateShippingAddressRequest();
        req.setOrderId(proto.getOrderId());
        req.setAlamat(proto.getAlamat());
        req.setProvinsi(proto.getProvinsi());
        req.setKota(proto.getKota());
        req.setCourier(proto.getCourier());
        req.setShippingMethod(proto.getShippingMethod());
        req.setShippingCost(proto.getShippingCost());
        req.setNegara(proto.getNegara());
        return req;
    }

    private UpdateShippingAddressRequest toUpdateShipping(
            com.sanedge.ecommerce.pb.shipping_address.UpdateShippingAddressRequest proto) {
        UpdateShippingAddressRequest req = new UpdateShippingAddressRequest();
        req.setShippingId(proto.getShippingId());
        req.setOrderId(proto.getOrderId());
        req.setAlamat(proto.getAlamat());
        req.setProvinsi(proto.getProvinsi());
        req.setKota(proto.getKota());
        req.setCourier(proto.getCourier());
        req.setShippingMethod(proto.getShippingMethod());
        req.setShippingCost(proto.getShippingCost());
        req.setNegara(proto.getNegara());
        return req;
    }

    private OrderResponse toProto(com.sanedge.ecommerce.order.domain.responses.order.OrderResponse r) {
        if (r == null) {
            return OrderResponse.getDefaultInstance();
        }
        return OrderResponse.newBuilder()
                .setId(r.getId() == null ? 0 : r.getId().intValue())
                .setMerchantId(r.getMerchantId() == null ? 0 : r.getMerchantId())
                .setUserId(r.getUserId() == null ? 0 : r.getUserId())
                .setTotalPrice(r.getTotalPrice() == null ? 0 : r.getTotalPrice())
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .build();
    }

    private OrderResponseDeleteAt toProtoDeleteAt(
            com.sanedge.ecommerce.order.domain.responses.order.OrderResponseDeleteAt r) {
        if (r == null) {
            return OrderResponseDeleteAt.getDefaultInstance();
        }
        return OrderResponseDeleteAt.newBuilder()
                .setId(r.getId() == null ? 0 : r.getId().intValue())
                .setMerchantId(r.getMerchantId() == null ? 0 : r.getMerchantId())
                .setUserId(r.getUserId() == null ? 0 : r.getUserId())
                .setTotalPrice(r.getTotalPrice() == null ? 0 : r.getTotalPrice())
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .setDeletedAt(com.google.protobuf.StringValue.of(ProtoMappers.nvl(r.getDeletedAt())))
                .build();
    }

    private ApiResponseOrderDeleteAt toDeleteAt(
            ApiResponse<com.sanedge.ecommerce.order.domain.responses.order.OrderResponseDeleteAt> res) {
        return ApiResponseOrderDeleteAt.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()))
                .setData(toProtoDeleteAt(res.getData()))
                .build();
    }

    private ApiResponseOrderAll toAll(ApiResponse<Boolean> res) {
        return ApiResponseOrderAll.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()))
                .build();
    }
}
