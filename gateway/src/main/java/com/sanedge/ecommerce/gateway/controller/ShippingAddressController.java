package com.sanedge.ecommerce.gateway.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.protobuf.Empty;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.gateway.dto.requests.shipping.FindAllShippingAddress;
import com.sanedge.ecommerce.gateway.dto.responses.shipping.ShippingAddressResponse;
import com.sanedge.ecommerce.gateway.dto.responses.shipping.ShippingAddressResponseDeleteAt;
import com.sanedge.ecommerce.gateway.mapper.ProtoMapper;
import com.sanedge.ecommerce.pb.shipping_address.ShippingCommandServiceGrpc;
import com.sanedge.ecommerce.pb.shipping_address.ShippingQueryServiceGrpc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shipping-address")
public class ShippingAddressController {

    private final ShippingQueryServiceGrpc.ShippingQueryServiceBlockingStub shippingQueryStub;
    private final ShippingCommandServiceGrpc.ShippingCommandServiceBlockingStub shippingCommandStub;

    @GetMapping("")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponsePagination<List<ShippingAddressResponse>>> findAll(
            @Valid FindAllShippingAddress req) {
        var resp = shippingQueryStub.findAll(
                com.sanedge.ecommerce.pb.shipping_address.FindAllShippingRequest.newBuilder()
                        .setPage(nvl(req.getPage()))
                        .setPageSize(nvl(req.getPageSize()))
                        .setSearch(nvl(req.getSearch()))
                        .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<ShippingAddressResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.shippings(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<ShippingAddressResponseDeleteAt>>> findByActive(
            @Valid FindAllShippingAddress req) {
        var resp = shippingQueryStub.findByActive(
                com.sanedge.ecommerce.pb.shipping_address.FindAllShippingRequest.newBuilder()
                        .setPage(nvl(req.getPage()))
                        .setPageSize(nvl(req.getPageSize()))
                        .setSearch(nvl(req.getSearch()))
                        .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<ShippingAddressResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.shippingsDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<ShippingAddressResponseDeleteAt>>> findByTrashed(
            @Valid FindAllShippingAddress req) {
        var resp = shippingQueryStub.findByTrashed(
                com.sanedge.ecommerce.pb.shipping_address.FindAllShippingRequest.newBuilder()
                        .setPage(nvl(req.getPage()))
                        .setPageSize(nvl(req.getPageSize()))
                        .setSearch(nvl(req.getSearch()))
                        .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<ShippingAddressResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.shippingsDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/{shippingId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<ShippingAddressResponse>> findById(@PathVariable Integer shippingId) {
        var resp = shippingQueryStub.findById(
                com.sanedge.ecommerce.pb.shipping_address.FindByIdShippingRequest.newBuilder()
                        .setId(shippingId)
                        .build());
        return ResponseEntity.ok(ApiResponse.<ShippingAddressResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.shipping(resp.getData()))
                .build());
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<ShippingAddressResponse>> findByOrder(@PathVariable Integer orderId) {
        var resp = shippingQueryStub.findByOrder(
                com.sanedge.ecommerce.pb.shipping_address.FindByIdShippingRequest.newBuilder()
                        .setId(orderId)
                        .build());
        return ResponseEntity.ok(ApiResponse.<ShippingAddressResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.shipping(resp.getData()))
                .build());
    }

    @PostMapping("/trashed/{shippingId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<ShippingAddressResponseDeleteAt>> trash(@PathVariable Integer shippingId) {
        var resp = shippingCommandStub.trashedShipping(
                com.sanedge.ecommerce.pb.shipping_address.FindByIdShippingRequest.newBuilder()
                        .setId(shippingId)
                        .build());
        return ResponseEntity.ok(ApiResponse.<ShippingAddressResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.shippingDeleteAt(resp.getData()))
                .build());
    }

    @PostMapping("/restore/{shippingId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<ShippingAddressResponseDeleteAt>> restore(@PathVariable Integer shippingId) {
        var resp = shippingCommandStub.restoreShipping(
                com.sanedge.ecommerce.pb.shipping_address.FindByIdShippingRequest.newBuilder()
                        .setId(shippingId)
                        .build());
        return ResponseEntity.ok(ApiResponse.<ShippingAddressResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.shippingDeleteAt(resp.getData()))
                .build());
    }

    @DeleteMapping("/permanent/{shippingId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deletePermanent(@PathVariable Integer shippingId) {
        var resp = shippingCommandStub.deleteShippingPermanent(
                com.sanedge.ecommerce.pb.shipping_address.FindByIdShippingRequest.newBuilder()
                        .setId(shippingId)
                        .build());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/restore/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> restoreAll() {
        var resp = shippingCommandStub.restoreAllShipping(Empty.getDefaultInstance());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/permanent/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllPermanent() {
        var resp = shippingCommandStub.deleteAllShippingPermanent(Empty.getDefaultInstance());
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
