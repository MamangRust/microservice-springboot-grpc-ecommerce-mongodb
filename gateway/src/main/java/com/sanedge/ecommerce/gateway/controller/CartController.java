package com.sanedge.ecommerce.gateway.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.gateway.dto.requests.cart.CreateCartRequest;
import com.sanedge.ecommerce.gateway.dto.requests.cart.DeleteCartRequest;
import com.sanedge.ecommerce.gateway.dto.requests.cart.FindAllCartsRequest;
import com.sanedge.ecommerce.gateway.dto.responses.cart.CartResponse;
import com.sanedge.ecommerce.gateway.mapper.ProtoMapper;
import com.sanedge.ecommerce.pb.cart.CartCommandServiceGrpc;
import com.sanedge.ecommerce.pb.cart.CartQueryServiceGrpc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartQueryServiceGrpc.CartQueryServiceBlockingStub cartQueryStub;
    private final CartCommandServiceGrpc.CartCommandServiceBlockingStub cartCommandStub;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponsePagination<List<CartResponse>>> findAll(
            @ModelAttribute FindAllCartsRequest req) {
        var resp = cartQueryStub.findAll(com.sanedge.ecommerce.pb.cart.FindAllCartRequest.newBuilder()
                .setUserId(nvl(req.getUserId()))
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<CartResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.carts(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<CartResponse>> createCart(@Valid @RequestBody CreateCartRequest req) {
        var resp = cartCommandStub.create(com.sanedge.ecommerce.pb.cart.CreateCartRequest.newBuilder()
                .setQuantity(nvl(req.getQuantity()))
                .setProductId(nvl(req.getProductId()))
                .setUserId(nvl(req.getUserId()))
                .build());
        return ResponseEntity.ok(ApiResponse.<CartResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cart(resp.getData()))
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<Boolean>> deleteCart(@PathVariable Long id) {
        var resp = cartCommandStub.delete(com.sanedge.ecommerce.pb.cart.DeleteCartRequest.newBuilder()
                .setCartId(id == null ? 0 : id.intValue())
                .build());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/delete-all")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllCarts(@Valid @RequestBody DeleteCartRequest req) {
        var builder = com.sanedge.ecommerce.pb.cart.DeleteAllCartRequest.newBuilder();
        if (req.getCartIds() != null) {
            builder.addAllCartIds(req.getCartIds());
        }
        var resp = cartCommandStub.deleteAll(builder.build());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    private static int nvl(Integer value) {
        return value == null ? 0 : value;
    }

    private static String nvl(String value) {
        return value == null ? "" : value;
    }

    private static Boolean success(String status) {
        return "success".equalsIgnoreCase(status);
    }
}
