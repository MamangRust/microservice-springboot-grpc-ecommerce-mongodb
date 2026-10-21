package com.sanedge.ecommerce.cart.handler;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.cart.service.CartService;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.pb.cart.ApiResponseCart;
import com.sanedge.ecommerce.pb.cart.ApiResponseCartAll;
import com.sanedge.ecommerce.pb.cart.ApiResponseCartDelete;
import com.sanedge.ecommerce.pb.cart.CartCommandServiceGrpc;
import com.sanedge.ecommerce.pb.cart.CartResponse;
import com.sanedge.ecommerce.pb.cart.CreateCartRequest;
import com.sanedge.ecommerce.pb.cart.DeleteAllCartRequest;
import com.sanedge.ecommerce.pb.cart.DeleteCartRequest;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartCommandHandler extends CartCommandServiceGrpc.CartCommandServiceImplBase {

    private final CartService cartService;

    @Override
    public void create(CreateCartRequest request, StreamObserver<ApiResponseCart> observer) {
        try {
            com.sanedge.ecommerce.cart.domain.requests.CreateCartRequest req = new com.sanedge.ecommerce.cart.domain.requests.CreateCartRequest();
            req.setQuantity(request.getQuantity());
            req.setProductId(request.getProductId());
            req.setUserId(request.getUserId());

            ApiResponse<com.sanedge.ecommerce.cart.domain.responses.CartResponse> res = cartService.createCart(req);

            ApiResponseCart.Builder builder = ApiResponseCart.newBuilder()
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
    public void delete(DeleteCartRequest request, StreamObserver<ApiResponseCartDelete> observer) {
        try {
            ApiResponse<Boolean> res = cartService.deletePermanent((long) request.getCartId());
            observer.onNext(ApiResponseCartDelete.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void deleteAll(DeleteAllCartRequest request, StreamObserver<ApiResponseCartAll> observer) {
        try {
            com.sanedge.ecommerce.cart.domain.requests.DeleteCartRequest req = new com.sanedge.ecommerce.cart.domain.requests.DeleteCartRequest();
            req.setCartIds(request.getCartIdsList());

            ApiResponse<Boolean> res = cartService.deleteAllPermanently(req);
            observer.onNext(ApiResponseCartAll.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
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
