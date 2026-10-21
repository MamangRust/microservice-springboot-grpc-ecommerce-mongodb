package com.sanedge.ecommerce.cart.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.cart.domain.requests.FindAllCartsRequest;
import com.sanedge.ecommerce.cart.service.CartService;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.pb.cart.ApiResponsePaginationCart;
import com.sanedge.ecommerce.pb.cart.CartQueryServiceGrpc;
import com.sanedge.ecommerce.pb.cart.CartResponse;
import com.sanedge.ecommerce.pb.cart.FindAllCartRequest;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartQueryHandler extends CartQueryServiceGrpc.CartQueryServiceImplBase {

    private final CartService cartService;

    @Override
    public void findAll(FindAllCartRequest request, StreamObserver<ApiResponsePaginationCart> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.cart.domain.responses.CartResponse>> res = cartService
                    .findAll(toFindAll(request));

            ApiResponsePaginationCart.Builder builder = ApiResponsePaginationCart.newBuilder()
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

    private FindAllCartsRequest toFindAll(FindAllCartRequest request) {
        FindAllCartsRequest req = new FindAllCartsRequest();
        req.setUserId(request.getUserId());
        req.setPage(request.getPage());
        req.setPageSize(request.getPageSize());
        req.setSearch(request.getSearch());
        return req;
    }

    private CartResponse toProto(com.sanedge.ecommerce.cart.domain.responses.CartResponse r) {
        return CartResponse.newBuilder()
                .setId(r.getId() == null ? 0 : r.getId().intValue())
                .setUserId(r.getUserId() == null ? 0 : r.getUserId())
                .setProductId(r.getProductId() == null ? 0 : r.getProductId())
                .setName(ProtoMappers.nvl(r.getName()))
                .setPrice(r.getPrice() == null ? 0 : r.getPrice())
                .setImage(ProtoMappers.nvl(r.getImage()))
                .setQuantity(r.getQuantity() == null ? 0 : r.getQuantity())
                .setWeight(r.getWeight() == null ? 0 : r.getWeight())
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .build();
    }
}
