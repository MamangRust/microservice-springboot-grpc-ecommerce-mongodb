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
import com.sanedge.ecommerce.gateway.dto.requests.merchant.FindAllMerchantRequest;
import com.sanedge.ecommerce.gateway.dto.requests.merchantpolicy.CreateMerchantPolicyRequest;
import com.sanedge.ecommerce.gateway.dto.requests.merchantpolicy.UpdateMerchantPolicyRequest;
import com.sanedge.ecommerce.gateway.dto.responses.merchantpolicy.MerchantPoliciesResponse;
import com.sanedge.ecommerce.gateway.dto.responses.merchantpolicy.MerchantPoliciesResponseDeleteAt;
import com.sanedge.ecommerce.gateway.mapper.ProtoMapper;
import com.sanedge.ecommerce.pb.merchant_policy.MerchantPolicyCommandServiceGrpc;
import com.sanedge.ecommerce.pb.merchant_policy.MerchantPolicyQueryServiceGrpc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/merchant-policy")
public class MerchantPolicyController {

    private final MerchantPolicyQueryServiceGrpc.MerchantPolicyQueryServiceBlockingStub queryStub;
    private final MerchantPolicyCommandServiceGrpc.MerchantPolicyCommandServiceBlockingStub commandStub;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponsePagination<List<MerchantPoliciesResponse>>> findAll(
            @ModelAttribute FindAllMerchantRequest req) {
        var resp = queryStub.findAll(com.sanedge.ecommerce.pb.merchant.FindAllMerchantRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<MerchantPoliciesResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantPoliciesList(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<MerchantPoliciesResponse>> findById(@PathVariable Integer id) {
        var resp = queryStub.findById(com.sanedge.ecommerce.pb.merchant_policy.FindByIdMerchantPoliciesRequest
                .newBuilder().setId(id).build());
        return ResponseEntity.ok(ApiResponse.<MerchantPoliciesResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantPolicies(resp.getData()))
                .build());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<MerchantPoliciesResponseDeleteAt>>> findByActive(
            @ModelAttribute FindAllMerchantRequest req) {
        var resp = queryStub.findByActive(com.sanedge.ecommerce.pb.merchant.FindAllMerchantRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<MerchantPoliciesResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantPoliciesDeleteAtList(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<MerchantPoliciesResponseDeleteAt>>> findByTrashed(
            @ModelAttribute FindAllMerchantRequest req) {
        var resp = queryStub.findByTrashed(com.sanedge.ecommerce.pb.merchant.FindAllMerchantRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<MerchantPoliciesResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantPoliciesDeleteAtList(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantPoliciesResponse>> create(
            @Valid @RequestBody CreateMerchantPolicyRequest request) {
        var resp = commandStub.create(com.sanedge.ecommerce.pb.merchant_policy.CreateMerchantPoliciesRequest
                .newBuilder()
                .setMerchantId(nvl(request.getMerchantId()))
                .setPolicyType(nvl(request.getPolicyType()))
                .setTitle(nvl(request.getTitle()))
                .setDescription(nvl(request.getDescription()))
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<MerchantPoliciesResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantPolicies(resp.getData()))
                .build());
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantPoliciesResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateMerchantPolicyRequest request) {
        request.setMerchantPolicyId(id);
        var resp = commandStub.update(com.sanedge.ecommerce.pb.merchant_policy.UpdateMerchantPoliciesRequest
                .newBuilder()
                .setMerchantPolicyId(nvl(request.getMerchantPolicyId()))
                .setPolicyType(nvl(request.getPolicyType()))
                .setTitle(nvl(request.getTitle()))
                .setDescription(nvl(request.getDescription()))
                .build());
        return ResponseEntity.ok(ApiResponse.<MerchantPoliciesResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantPolicies(resp.getData()))
                .build());
    }

    @PostMapping("/trashed/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantPoliciesResponseDeleteAt>> trashed(@PathVariable Integer id) {
        var resp = commandStub.trashedMerchantPolicies(
                com.sanedge.ecommerce.pb.merchant_policy.FindByIdMerchantPoliciesRequest.newBuilder().setId(id)
                        .build());
        return ResponseEntity.ok(ApiResponse.<MerchantPoliciesResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantPoliciesDeleteAt(resp.getData()))
                .build());
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantPoliciesResponseDeleteAt>> restore(@PathVariable Integer id) {
        var resp = commandStub.restoreMerchantPolicies(
                com.sanedge.ecommerce.pb.merchant_policy.FindByIdMerchantPoliciesRequest.newBuilder().setId(id)
                        .build());
        return ResponseEntity.ok(ApiResponse.<MerchantPoliciesResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantPoliciesDeleteAt(resp.getData()))
                .build());
    }

    @DeleteMapping("/permanent/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deletePermanent(@PathVariable Integer id) {
        var resp = commandStub.deleteMerchantPoliciesPermanent(
                com.sanedge.ecommerce.pb.merchant_policy.FindByIdMerchantPoliciesRequest.newBuilder().setId(id)
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
        var resp = commandStub.restoreAllMerchantPolicies(Empty.getDefaultInstance());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/permanent/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllPermanent() {
        var resp = commandStub.deleteAllMerchantPoliciesPermanent(Empty.getDefaultInstance());
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
