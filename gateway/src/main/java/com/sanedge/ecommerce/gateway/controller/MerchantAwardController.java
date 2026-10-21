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

import com.google.protobuf.Empty;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.gateway.dto.requests.merchant.FindAllMerchantRequest;
import com.sanedge.ecommerce.gateway.dto.requests.merchantawrd.CreateMerchantAwardRequest;
import com.sanedge.ecommerce.gateway.dto.requests.merchantawrd.UpdateMerchantAwardRequest;
import com.sanedge.ecommerce.gateway.dto.responses.merchantaward.MerchantAwardResponse;
import com.sanedge.ecommerce.gateway.dto.responses.merchantaward.MerchantAwardResponseDeleteAt;
import com.sanedge.ecommerce.gateway.mapper.ProtoMapper;
import com.sanedge.ecommerce.pb.merchant_award.MerchantAwardCommandServiceGrpc;
import com.sanedge.ecommerce.pb.merchant_award.MerchantAwardQueryServiceGrpc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/merchant-award")
public class MerchantAwardController {

    private final MerchantAwardQueryServiceGrpc.MerchantAwardQueryServiceBlockingStub queryStub;
    private final MerchantAwardCommandServiceGrpc.MerchantAwardCommandServiceBlockingStub commandStub;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponsePagination<List<MerchantAwardResponse>>> findAll(
            @ModelAttribute FindAllMerchantRequest req) {
        var resp = queryStub.findAll(com.sanedge.ecommerce.pb.merchant.FindAllMerchantRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<MerchantAwardResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantAwards(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<MerchantAwardResponse>> findById(@PathVariable Integer id) {
        var resp = queryStub.findById(
                com.sanedge.ecommerce.pb.merchant_award.FindByIdMerchantAwardRequest.newBuilder().setId(id).build());
        return ResponseEntity.ok(ApiResponse.<MerchantAwardResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantAward(resp.getData()))
                .build());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponsePagination<List<MerchantAwardResponseDeleteAt>>> findByActive(
            @ModelAttribute FindAllMerchantRequest req) {
        var resp = queryStub.findByActive(com.sanedge.ecommerce.pb.merchant.FindAllMerchantRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<MerchantAwardResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantAwardsDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponsePagination<List<MerchantAwardResponseDeleteAt>>> findByTrashed(
            @ModelAttribute FindAllMerchantRequest req) {
        var resp = queryStub.findByTrashed(com.sanedge.ecommerce.pb.merchant.FindAllMerchantRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<MerchantAwardResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantAwardsDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantAwardResponse>> create(
            @Valid @RequestBody CreateMerchantAwardRequest req) {
        var resp = commandStub.create(com.sanedge.ecommerce.pb.merchant_award.CreateMerchantAwardRequest.newBuilder()
                .setMerchantId(nvl(req.getMerchantId()))
                .setTitle(nvl(req.getTitle()))
                .setDescription(nvl(req.getDescription()))
                .setIssuedBy(nvl(req.getIssuedBy()))
                .setIssueDate(nvl(req.getIssueDate()))
                .setExpiryDate(nvl(req.getExpiryDate()))
                .setCertificateUrl(nvl(req.getCertificateUrl()))
                .build());
        return ResponseEntity.ok(ApiResponse.<MerchantAwardResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantAward(resp.getData()))
                .build());
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantAwardResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateMerchantAwardRequest req) {
        req.setMerchantCertificationId(id);
        var resp = commandStub.update(com.sanedge.ecommerce.pb.merchant_award.UpdateMerchantAwardRequest.newBuilder()
                .setMerchantCertificationId(nvl(req.getMerchantCertificationId()))
                .setTitle(nvl(req.getTitle()))
                .setDescription(nvl(req.getDescription()))
                .setIssuedBy(nvl(req.getIssuedBy()))
                .setIssueDate(nvl(req.getIssueDate()))
                .setExpiryDate(nvl(req.getExpiryDate()))
                .setCertificateUrl(nvl(req.getCertificateUrl()))
                .build());
        return ResponseEntity.ok(ApiResponse.<MerchantAwardResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantAward(resp.getData()))
                .build());
    }

    @PostMapping("/trashed/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantAwardResponseDeleteAt>> trashed(@PathVariable Integer id) {
        var resp = commandStub.trashedMerchantAward(
                com.sanedge.ecommerce.pb.merchant_award.FindByIdMerchantAwardRequest.newBuilder().setId(id).build());
        return ResponseEntity.ok(ApiResponse.<MerchantAwardResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantAwardDeleteAt(resp.getData()))
                .build());
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantAwardResponseDeleteAt>> restore(@PathVariable Integer id) {
        var resp = commandStub.restoreMerchantAward(
                com.sanedge.ecommerce.pb.merchant_award.FindByIdMerchantAwardRequest.newBuilder().setId(id).build());
        return ResponseEntity.ok(ApiResponse.<MerchantAwardResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantAwardDeleteAt(resp.getData()))
                .build());
    }

    @DeleteMapping("/permanent/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deletePermanent(@PathVariable Integer id) {
        var resp = commandStub.deleteMerchantAwardPermanent(
                com.sanedge.ecommerce.pb.merchant_award.FindByIdMerchantAwardRequest.newBuilder().setId(id).build());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/restore/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> restoreAll() {
        var resp = commandStub.restoreAllMerchantAward(Empty.getDefaultInstance());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/permanent/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllPermanent() {
        var resp = commandStub.deleteAllMerchantAwardPermanent(Empty.getDefaultInstance());
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
