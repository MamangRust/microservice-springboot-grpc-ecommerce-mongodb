package com.sanedge.ecommerce.order_item.handler;

import org.springframework.stereotype.Service;

import com.google.protobuf.Empty;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.order_item.service.OrderItemCommandService;
import com.sanedge.ecommerce.pb.order_item.ApiResponseOrderItem;
import com.sanedge.ecommerce.pb.order_item.ApiResponseOrderItemAll;
import com.sanedge.ecommerce.pb.order_item.ApiResponseOrderItemDelete;
import com.sanedge.ecommerce.pb.order_item.CalculateTotalPriceRequest;
import com.sanedge.ecommerce.pb.order_item.CalculateTotalPriceResponse;
import com.sanedge.ecommerce.pb.order_item.CreateOrderItemRecordRequest;
import com.sanedge.ecommerce.pb.order_item.FindByIdOrderItemRequest;
import com.sanedge.ecommerce.pb.order_item.OrderItemCommandServiceGrpc;
import com.sanedge.ecommerce.pb.order_item.OrderItemResponse;
import com.sanedge.ecommerce.pb.order_item.UpdateOrderItemRecordRequest;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderItemCommandHandler extends OrderItemCommandServiceGrpc.OrderItemCommandServiceImplBase {

    private final OrderItemCommandService orderItemCommandService;

    @Override
    public void createOrderItem(CreateOrderItemRecordRequest request, StreamObserver<ApiResponseOrderItem> observer) {
        try {
            com.sanedge.ecommerce.order_item.domain.requests.CreateOrderItemRequest req = new com.sanedge.ecommerce.order_item.domain.requests.CreateOrderItemRequest();
            req.setOrderId(request.getOrderId());
            req.setProductId(request.getProductId());
            req.setQuantity(request.getQuantity());
            req.setPrice(request.getPrice());

            ApiResponse<com.sanedge.ecommerce.order_item.domain.responses.OrderItemResponse> res = orderItemCommandService
                    .create(req);

            observer.onNext(toApiResponse(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void updateOrderItem(UpdateOrderItemRecordRequest request, StreamObserver<ApiResponseOrderItem> observer) {
        try {
            com.sanedge.ecommerce.order_item.domain.requests.UpdateOrderItemRequest req = new com.sanedge.ecommerce.order_item.domain.requests.UpdateOrderItemRequest();
            req.setOrderItemId(request.getOrderItemId());
            req.setQuantity(request.getQuantity());
            req.setPrice(request.getPrice());

            ApiResponse<com.sanedge.ecommerce.order_item.domain.responses.OrderItemResponse> res = orderItemCommandService
                    .update(req);

            observer.onNext(toApiResponse(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void trashOrderItem(FindByIdOrderItemRequest request, StreamObserver<ApiResponseOrderItem> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.order_item.domain.responses.OrderItemResponseDeleteAt> res = orderItemCommandService
                    .trash((long) request.getId());

            observer.onNext(toApiResponseDeleteAt(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void restoreOrderItem(FindByIdOrderItemRequest request, StreamObserver<ApiResponseOrderItem> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.order_item.domain.responses.OrderItemResponseDeleteAt> res = orderItemCommandService
                    .restore((long) request.getId());

            observer.onNext(toApiResponseDeleteAt(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteOrderItemPermanent(FindByIdOrderItemRequest request,
            StreamObserver<ApiResponseOrderItemDelete> observer) {
        try {
            ApiResponse<Boolean> res = orderItemCommandService.deletePermanent((long) request.getId());

            observer.onNext(ApiResponseOrderItemDelete.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void restoreAllOrdersItem(Empty request, StreamObserver<ApiResponseOrderItemAll> observer) {
        try {
            ApiResponse<Boolean> res = orderItemCommandService.restoreAll();

            observer.onNext(toAll(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteAllPermanentOrdersItem(Empty request, StreamObserver<ApiResponseOrderItemAll> observer) {
        try {
            ApiResponse<Boolean> res = orderItemCommandService.deleteAll();

            observer.onNext(toAll(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteOrderItemByOrderPermanent(FindByIdOrderItemRequest request,
            StreamObserver<ApiResponseOrderItemDelete> observer) {
        try {
            ApiResponse<Boolean> res = orderItemCommandService.deleteByOrderPermanent((long) request.getId());

            observer.onNext(ApiResponseOrderItemDelete.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteOrderItemByOrderRollback(FindByIdOrderItemRequest request,
            StreamObserver<ApiResponseOrderItemDelete> observer) {
        try {
            ApiResponse<Boolean> res = orderItemCommandService.deleteByOrderRollback((long) request.getId());

            observer.onNext(ApiResponseOrderItemDelete.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void calculateTotalPrice(CalculateTotalPriceRequest request,
            StreamObserver<CalculateTotalPriceResponse> observer) {
        try {
            ApiResponse<Integer> res = orderItemCommandService.calculateTotalPrice((long) request.getOrderId());

            observer.onNext(CalculateTotalPriceResponse.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setTotalPrice(res.getData() == null ? 0 : res.getData())
                    .build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private ApiResponseOrderItem toApiResponse(
            ApiResponse<com.sanedge.ecommerce.order_item.domain.responses.OrderItemResponse> res) {
        ApiResponseOrderItem.Builder builder = ApiResponseOrderItem.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()));
        if (res.getData() != null) {
            builder.setData(toProto(res.getData()));
        }
        return builder.build();
    }

    private ApiResponseOrderItem toApiResponseDeleteAt(
            ApiResponse<com.sanedge.ecommerce.order_item.domain.responses.OrderItemResponseDeleteAt> res) {
        ApiResponseOrderItem.Builder builder = ApiResponseOrderItem.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()));
        if (res.getData() != null) {
            builder.setData(toProto(res.getData()));
        }
        return builder.build();
    }

    private ApiResponseOrderItemAll toAll(ApiResponse<Boolean> res) {
        return ApiResponseOrderItemAll.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()))
                .build();
    }

    private OrderItemResponse toProto(com.sanedge.ecommerce.order_item.domain.responses.OrderItemResponse r) {
        if (r == null) {
            return OrderItemResponse.getDefaultInstance();
        }
        return OrderItemResponse.newBuilder()
                .setId(r.getId() == null ? 0 : r.getId().intValue())
                .setOrderId(r.getOrderId() == null ? 0 : r.getOrderId())
                .setProductId(r.getProductId() == null ? 0 : r.getProductId())
                .setQuantity(r.getQuantity() == null ? 0 : r.getQuantity())
                .setPrice(r.getPrice() == null ? 0 : r.getPrice())
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .build();
    }

    private OrderItemResponse toProto(com.sanedge.ecommerce.order_item.domain.responses.OrderItemResponseDeleteAt r) {
        if (r == null) {
            return OrderItemResponse.getDefaultInstance();
        }
        return OrderItemResponse.newBuilder()
                .setId(r.getId() == null ? 0 : r.getId().intValue())
                .setOrderId(r.getOrderId() == null ? 0 : r.getOrderId())
                .setProductId(r.getProductId() == null ? 0 : r.getProductId())
                .setQuantity(r.getQuantity() == null ? 0 : r.getQuantity())
                .setPrice(r.getPrice() == null ? 0 : r.getPrice())
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .build();
    }
}
