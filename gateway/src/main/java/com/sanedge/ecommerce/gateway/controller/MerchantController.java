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
import org.springframework.web.bind.annotation.RestController;

import com.google.protobuf.Empty;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.gateway.dto.requests.merchant.CreateMerchantRequest;
import com.sanedge.ecommerce.gateway.dto.requests.merchant.FindAllMerchantRequest;
import com.sanedge.ecommerce.gateway.dto.requests.merchant.UpdateMerchantRequest;
import com.sanedge.ecommerce.gateway.dto.responses.merchant.MerchantResponse;
import com.sanedge.ecommerce.gateway.dto.responses.merchant.MerchantResponseDeleteAt;
import com.sanedge.ecommerce.gateway.mapper.ProtoMapper;
import com.sanedge.ecommerce.pb.merchant.MerchantCommandServiceGrpc;
import com.sanedge.ecommerce.pb.merchant.MerchantQueryServiceGrpc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/merchant")
public class MerchantController {

    private final MerchantQueryServiceGrpc.MerchantQueryServiceBlockingStub merchantQueryStub;
    private final MerchantCommandServiceGrpc.MerchantCommandServiceBlockingStub merchantCommandStub;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponsePagination<List<MerchantResponse>>> findAll(
            @ModelAttribute FindAllMerchantRequest req) {
        var resp = merchantQueryStub.findAll(com.sanedge.ecommerce.pb.merchant.FindAllMerchantRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<MerchantResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchants(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponsePagination<List<MerchantResponseDeleteAt>>> findByActive(
            @ModelAttribute FindAllMerchantRequest req) {
        var resp = merchantQueryStub.findByActive(com.sanedge.ecommerce.pb.merchant.FindAllMerchantRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<MerchantResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantsDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponsePagination<List<MerchantResponseDeleteAt>>> findByTrashed(
            @ModelAttribute FindAllMerchantRequest req) {
        var resp = merchantQueryStub.findByTrashed(com.sanedge.ecommerce.pb.merchant.FindAllMerchantRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<MerchantResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantsDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<MerchantResponse>> findById(@PathVariable Integer id) {
        var resp = merchantQueryStub.findById(com.sanedge.ecommerce.pb.merchant.FindByIdMerchantRequest.newBuilder()
                .setId(id)
                .build());
        return ResponseEntity.ok(ApiResponse.<MerchantResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchant(resp.getData()))
                .build());
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantResponse>> createMerchant(
            @Valid @RequestBody CreateMerchantRequest req) {
        var resp = merchantCommandStub.create(com.sanedge.ecommerce.pb.merchant.CreateMerchantRequest.newBuilder()
                .setUserId(nvl(req.getUserId()))
                .setName(nvl(req.getName()))
                .setDescription(nvl(req.getDescription()))
                .setAddress(nvl(req.getAddress()))
                .setContactEmail(nvl(req.getContactEmail()))
                .setContactPhone(nvl(req.getContactPhone()))
                .setStatus(nvl(req.getStatus()))
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<MerchantResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchant(resp.getData()))
                .build());
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantResponse>> updateMerchant(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateMerchantRequest req) {
        req.setMerchantId(id);
        var resp = merchantCommandStub.update(com.sanedge.ecommerce.pb.merchant.UpdateMerchantRequest.newBuilder()
                .setMerchantId(nvl(req.getMerchantId()))
                .setUserId(nvl(req.getUserId()))
                .setName(nvl(req.getName()))
                .setDescription(nvl(req.getDescription()))
                .setAddress(nvl(req.getAddress()))
                .setContactEmail(nvl(req.getContactEmail()))
                .setContactPhone(nvl(req.getContactPhone()))
                .setStatus(nvl(req.getStatus()))
                .build());
        return ResponseEntity.ok(ApiResponse.<MerchantResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchant(resp.getData()))
                .build());
    }

    @PostMapping("/trashed/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantResponseDeleteAt>> trashedMerchant(@PathVariable Integer id) {
        var resp = merchantCommandStub.trashedMerchant(
                com.sanedge.ecommerce.pb.merchant.FindByIdMerchantRequest.newBuilder().setId(id).build());
        return ResponseEntity.ok(ApiResponse.<MerchantResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantDeleteAt(resp.getData()))
                .build());
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantResponseDeleteAt>> restoreMerchant(@PathVariable Integer id) {
        var resp = merchantCommandStub.restoreMerchant(
                com.sanedge.ecommerce.pb.merchant.FindByIdMerchantRequest.newBuilder().setId(id).build());
        return ResponseEntity.ok(ApiResponse.<MerchantResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantDeleteAtFromResponse(resp.getData()))
                .build());
    }

    @DeleteMapping("/permanent/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteMerchantPermanent(@PathVariable Integer id) {
        var resp = merchantCommandStub.deleteMerchantPermanent(
                com.sanedge.ecommerce.pb.merchant.FindByIdMerchantRequest.newBuilder().setId(id).build());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/restore/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> restoreAllMerchant() {
        var resp = merchantCommandStub.restoreAllMerchant(Empty.getDefaultInstance());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/permanent/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllMerchantPermanent() {
        var resp = merchantCommandStub.deleteAllMerchantPermanent(Empty.getDefaultInstance());
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
