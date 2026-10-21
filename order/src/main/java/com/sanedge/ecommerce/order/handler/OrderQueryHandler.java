package com.sanedge.ecommerce.order.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.order.service.OrderQueryService;
import com.sanedge.ecommerce.pb.order.ApiResponseOrder;
import com.sanedge.ecommerce.pb.order.ApiResponsePaginationOrder;
import com.sanedge.ecommerce.pb.order.ApiResponsePaginationOrderDeleteAt;
import com.sanedge.ecommerce.pb.order.FindAllOrderRequest;
import com.sanedge.ecommerce.pb.order.FindByIdOrderRequest;
import com.sanedge.ecommerce.pb.order.OrderQueryServiceGrpc;
import com.sanedge.ecommerce.pb.order.OrderResponse;
import com.sanedge.ecommerce.pb.order.OrderResponseDeleteAt;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderQueryHandler extends OrderQueryServiceGrpc.OrderQueryServiceImplBase {

    private final OrderQueryService orderQueryService;

    @Override
    public void findAll(FindAllOrderRequest request, StreamObserver<ApiResponsePaginationOrder> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.order.domain.responses.order.OrderResponse>> res = orderQueryService
                    .findAll(toFindAll(request));

            ApiResponsePaginationOrder.Builder builder = ApiResponsePaginationOrder.newBuilder()
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
    public void findById(FindByIdOrderRequest request, StreamObserver<ApiResponseOrder> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.order.domain.responses.order.OrderResponse> res = orderQueryService
                    .findById(request.getId());

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
    public void findByActive(FindAllOrderRequest request,
            StreamObserver<ApiResponsePaginationOrderDeleteAt> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.order.domain.responses.order.OrderResponseDeleteAt>> res = orderQueryService
                    .findByActive(toFindAll(request));

            ApiResponsePaginationOrderDeleteAt.Builder builder = ApiResponsePaginationOrderDeleteAt.newBuilder()
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
    public void findByTrashed(FindAllOrderRequest request,
            StreamObserver<ApiResponsePaginationOrderDeleteAt> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.order.domain.responses.order.OrderResponseDeleteAt>> res = orderQueryService
                    .findByTrashed(toFindAll(request));

            ApiResponsePaginationOrderDeleteAt.Builder builder = ApiResponsePaginationOrderDeleteAt.newBuilder()
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

    private com.sanedge.ecommerce.order.domain.requests.order.FindAllOrderRequest toFindAll(
            FindAllOrderRequest request) {
        com.sanedge.ecommerce.order.domain.requests.order.FindAllOrderRequest req = new com.sanedge.ecommerce.order.domain.requests.order.FindAllOrderRequest();
        req.setPage(request.getPage());
        req.setPageSize(request.getPageSize());
        req.setSearch(request.getSearch());
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
}
