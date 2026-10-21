package com.sanedge.ecommerce.order_item.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.order_item.domain.requests.FindAllOrderItems;
import com.sanedge.ecommerce.order_item.service.OrderItemQueryService;
import com.sanedge.ecommerce.pb.order_item.ApiResponsePaginationOrderItem;
import com.sanedge.ecommerce.pb.order_item.ApiResponsePaginationOrderItemDeleteAt;
import com.sanedge.ecommerce.pb.order_item.ApiResponsesOrderItem;
import com.sanedge.ecommerce.pb.order_item.FindAllOrderItemRequest;
import com.sanedge.ecommerce.pb.order_item.FindByIdOrderItemRequest;
import com.sanedge.ecommerce.pb.order_item.OrderItemQueryServiceGrpc;
import com.sanedge.ecommerce.pb.order_item.OrderItemResponse;
import com.sanedge.ecommerce.pb.order_item.OrderItemResponseDeleteAt;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderItemQueryHandler extends OrderItemQueryServiceGrpc.OrderItemQueryServiceImplBase {

    private final OrderItemQueryService orderItemQueryService;

    @Override
    public void findAll(FindAllOrderItemRequest request, StreamObserver<ApiResponsePaginationOrderItem> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.order_item.domain.responses.OrderItemResponse>> res = orderItemQueryService
                    .findAll(toFindAll(request));

            ApiResponsePaginationOrderItem.Builder builder = ApiResponsePaginationOrderItem.newBuilder()
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
    public void findByActive(FindAllOrderItemRequest request,
            StreamObserver<ApiResponsePaginationOrderItemDeleteAt> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.order_item.domain.responses.OrderItemResponseDeleteAt>> res = orderItemQueryService
                    .findByActive(toFindAll(request));

            ApiResponsePaginationOrderItemDeleteAt.Builder builder = ApiResponsePaginationOrderItemDeleteAt.newBuilder()
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
    public void findByTrashed(FindAllOrderItemRequest request,
            StreamObserver<ApiResponsePaginationOrderItemDeleteAt> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.order_item.domain.responses.OrderItemResponseDeleteAt>> res = orderItemQueryService
                    .findByTrashed(toFindAll(request));

            ApiResponsePaginationOrderItemDeleteAt.Builder builder = ApiResponsePaginationOrderItemDeleteAt.newBuilder()
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
    public void findOrderItemByOrder(FindByIdOrderItemRequest request, StreamObserver<ApiResponsesOrderItem> observer) {
        try {
            ApiResponse<List<com.sanedge.ecommerce.order_item.domain.responses.OrderItemResponse>> res = orderItemQueryService
                    .findByOrder((long) request.getId());

            ApiResponsesOrderItem.Builder builder = ApiResponsesOrderItem.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            if (res.getData() != null) {
                res.getData().forEach(r -> builder.addData(toProto(r)));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private FindAllOrderItems toFindAll(FindAllOrderItemRequest request) {
        FindAllOrderItems req = new FindAllOrderItems();
        req.setPage(request.getPage());
        req.setPageSize(request.getPageSize());
        req.setSearch(request.getSearch());
        return req;
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

    private OrderItemResponseDeleteAt toProtoDeleteAt(
            com.sanedge.ecommerce.order_item.domain.responses.OrderItemResponseDeleteAt r) {
        if (r == null) {
            return OrderItemResponseDeleteAt.getDefaultInstance();
        }
        return OrderItemResponseDeleteAt.newBuilder()
                .setId(r.getId() == null ? 0 : r.getId().intValue())
                .setOrderId(r.getOrderId() == null ? 0 : r.getOrderId())
                .setProductId(r.getProductId() == null ? 0 : r.getProductId())
                .setQuantity(r.getQuantity() == null ? 0 : r.getQuantity())
                .setPrice(r.getPrice() == null ? 0 : r.getPrice())
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .setDeletedAt(com.google.protobuf.StringValue.of(ProtoMappers.nvl(r.getDeletedAt())))
                .build();
    }
}
