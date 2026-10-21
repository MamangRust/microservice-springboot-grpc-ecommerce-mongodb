package com.sanedge.ecommerce.gateway.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.protobuf.Empty;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.gateway.dto.requests.order.CreateOrderRequest;
import com.sanedge.ecommerce.gateway.dto.requests.order.FindAllOrderByMerchantRequest;
import com.sanedge.ecommerce.gateway.dto.requests.order.FindAllOrderRequest;
import com.sanedge.ecommerce.gateway.dto.requests.order.MonthOrderMerchantRequest;
import com.sanedge.ecommerce.gateway.dto.requests.order.MonthTotalRevenue;
import com.sanedge.ecommerce.gateway.dto.requests.order.MonthTotalRevenueMerchantRequest;
import com.sanedge.ecommerce.gateway.dto.requests.order.UpdateOrderRequest;
import com.sanedge.ecommerce.gateway.dto.requests.order.YearOrderMerchantRequest;
import com.sanedge.ecommerce.gateway.dto.requests.order.YearTotalRevenueMerchantRequest;
import com.sanedge.ecommerce.gateway.dto.responses.order.OrderMonthlyResponse;
import com.sanedge.ecommerce.gateway.dto.responses.order.OrderMonthlyTotalRevenueResponse;
import com.sanedge.ecommerce.gateway.dto.responses.order.OrderResponse;
import com.sanedge.ecommerce.gateway.dto.responses.order.OrderResponseDeleteAt;
import com.sanedge.ecommerce.gateway.dto.responses.order.OrderYearlyResponse;
import com.sanedge.ecommerce.gateway.dto.responses.order.OrderYearlyTotalRevenueResponse;
import com.sanedge.ecommerce.gateway.mapper.ProtoMapper;
import com.sanedge.ecommerce.pb.order.OrderCommandServiceGrpc;
import com.sanedge.ecommerce.pb.order.OrderQueryServiceGrpc;
import com.sanedge.ecommerce.pb.shipping_address.CreateShippingAddressRequest;
import com.sanedge.ecommerce.pb.shipping_address.UpdateShippingAddressRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private final OrderQueryServiceGrpc.OrderQueryServiceBlockingStub orderQueryStub;
    private final OrderCommandServiceGrpc.OrderCommandServiceBlockingStub orderCommandStub;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponsePagination<List<OrderResponse>>> findAll(
            @ModelAttribute FindAllOrderRequest req) {
        var resp = orderQueryStub.findAll(com.sanedge.ecommerce.pb.order.FindAllOrderRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<OrderResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.orders(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<OrderResponse>> findById(@PathVariable Integer id) {
        var resp = orderQueryStub.findById(com.sanedge.ecommerce.pb.order.FindByIdOrderRequest.newBuilder()
                .setId(id)
                .build());
        return ResponseEntity.ok(ApiResponse.<OrderResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.order(resp.getData()))
                .build());
    }

    @GetMapping("/relation/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<com.sanedge.ecommerce.gateway.dto.responses.order.OrderRelationResponse>> findByOrderId(
            @PathVariable Integer id) {
        // The order gRPC contract does not expose an order-relation query.
        throw new UnsupportedOperationException("Order relation endpoint is not available over gRPC");
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponsePagination<List<OrderResponseDeleteAt>>> findByActive(
            @ModelAttribute FindAllOrderRequest req) {
        var resp = orderQueryStub.findByActive(com.sanedge.ecommerce.pb.order.FindAllOrderRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<OrderResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.ordersDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponsePagination<List<OrderResponseDeleteAt>>> findByTrashed(
            @ModelAttribute FindAllOrderRequest req) {
        var resp = orderQueryStub.findByTrashed(com.sanedge.ecommerce.pb.order.FindAllOrderRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<OrderResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.ordersDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/merchant/{merchant_id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<OrderResponse>>> findByMerchant(
            @PathVariable Integer merchant_id,
            @ModelAttribute FindAllOrderByMerchantRequest req) {
        // The order gRPC contract does not expose a by-merchant query.
        throw new UnsupportedOperationException("Order by-merchant endpoint is not available over gRPC");
    }


    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> create(@Valid @RequestBody CreateOrderRequest req) {
        var builder = com.sanedge.ecommerce.pb.order.CreateOrderRequest.newBuilder()
                .setMerchantId(nvl(req.getMerchantId()))
                .setUserId(nvl(req.getUserId()));
        if (req.getItems() != null) {
            req.getItems().forEach(i -> builder.addItems(com.sanedge.ecommerce.pb.order.CreateOrderItemRequest
                    .newBuilder()
                    .setProductId(nvl(i.getProductId()))
                    .setQuantity(nvl(i.getQuantity()))
                    .setPrice(nvl(i.getPrice()))
                    .build()));
        }
        if (req.getShippingAddress() != null) {
            builder.setShipping(toProtoCreateShipping(req.getShippingAddress()));
        }
        var resp = orderCommandStub.create(builder.build());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<OrderResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.order(resp.getData()))
                .build());
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateOrderRequest req) {
        req.setOrderId(id);
        var builder = com.sanedge.ecommerce.pb.order.UpdateOrderRequest.newBuilder()
                .setOrderId(nvl(req.getOrderId()))
                .setUserId(nvl(req.getUserId()));
        if (req.getItems() != null) {
            req.getItems().forEach(i -> builder.addItems(com.sanedge.ecommerce.pb.order.UpdateOrderItemRequest
                    .newBuilder()
                    .setOrderItemId(nvl(i.getOrderItemId()))
                    .setProductId(nvl(i.getProductId()))
                    .setQuantity(nvl(i.getQuantity()))
                    .setPrice(nvl(i.getPrice()))
                    .build()));
        }
        if (req.getShippingAddress() != null) {
            builder.setShipping(toProtoUpdateShipping(req.getShippingAddress()));
        }
        var resp = orderCommandStub.update(builder.build());
        return ResponseEntity.ok(ApiResponse.<OrderResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.order(resp.getData()))
                .build());
    }

    @PostMapping("/trashed/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponseDeleteAt>> trashedOrder(@PathVariable Integer id) {
        var resp = orderCommandStub.trashedOrder(
                com.sanedge.ecommerce.pb.order.FindByIdOrderRequest.newBuilder().setId(id).build());
        return ResponseEntity.ok(ApiResponse.<OrderResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.orderDeleteAt(resp.getData()))
                .build());
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponseDeleteAt>> restoreOrder(@PathVariable Integer id) {
        var resp = orderCommandStub.restoreOrder(
                com.sanedge.ecommerce.pb.order.FindByIdOrderRequest.newBuilder().setId(id).build());
        return ResponseEntity.ok(ApiResponse.<OrderResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.orderDeleteAt(resp.getData()))
                .build());
    }

    @DeleteMapping("/permanent/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteOrderPermanent(@PathVariable Integer id) {
        var resp = orderCommandStub.deleteOrderPermanent(
                com.sanedge.ecommerce.pb.order.FindByIdOrderRequest.newBuilder().setId(id).build());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/restore/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> restoreAllOrders() {
        var resp = orderCommandStub.restoreAllOrder(Empty.getDefaultInstance());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/permanent/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllOrdersPermanent() {
        var resp = orderCommandStub.deleteAllOrderPermanent(Empty.getDefaultInstance());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    private static CreateShippingAddressRequest toProtoCreateShipping(
            com.sanedge.ecommerce.gateway.dto.requests.shipping.CreateShippingAddressRequest s) {
        return CreateShippingAddressRequest.newBuilder()
                .setOrderId(nvl(s.getOrderId()))
                .setAlamat(nvl(s.getAlamat()))
                .setProvinsi(nvl(s.getProvinsi()))
                .setKota(nvl(s.getKota()))
                .setCourier(nvl(s.getCourier()))
                .setShippingMethod(nvl(s.getShippingMethod()))
                .setShippingCost(nvl(s.getShippingCost()))
                .setNegara(nvl(s.getNegara()))
                .build();
    }

    private static UpdateShippingAddressRequest toProtoUpdateShipping(
            com.sanedge.ecommerce.gateway.dto.requests.shipping.UpdateShippingAddressRequest s) {
        return UpdateShippingAddressRequest.newBuilder()
                .setShippingId(nvl(s.getShippingId()))
                .setOrderId(nvl(s.getOrderId()))
                .setAlamat(nvl(s.getAlamat()))
                .setProvinsi(nvl(s.getProvinsi()))
                .setKota(nvl(s.getKota()))
                .setCourier(nvl(s.getCourier()))
                .setShippingMethod(nvl(s.getShippingMethod()))
                .setShippingCost(nvl(s.getShippingCost()))
                .setNegara(nvl(s.getNegara()))
                .build();
    }

    private static int yearOf(Integer yearMonth) {
        return yearMonth == null ? 0 : yearMonth / 100;
    }

    private static int monthOf(Integer yearMonth) {
        return yearMonth == null ? 0 : yearMonth % 100;
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
