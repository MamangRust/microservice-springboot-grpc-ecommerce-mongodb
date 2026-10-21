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
import com.sanedge.ecommerce.gateway.dto.requests.merchantbusiness.CreateMerchantBusinessRequest;
import com.sanedge.ecommerce.gateway.dto.requests.merchantbusiness.UpdateMerchantBusinessRequest;
import com.sanedge.ecommerce.gateway.dto.responses.merchantbusiness.MerchantBusinessResponse;
import com.sanedge.ecommerce.gateway.dto.responses.merchantbusiness.MerchantBusinessResponseDeleteAt;
import com.sanedge.ecommerce.gateway.mapper.ProtoMapper;
import com.sanedge.ecommerce.pb.merchant_business.MerchantBusinessCommandServiceGrpc;
import com.sanedge.ecommerce.pb.merchant_business.MerchantBusinessQueryServiceGrpc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/merchant-business")
public class MerchantBusinessController {

    private final MerchantBusinessQueryServiceGrpc.MerchantBusinessQueryServiceBlockingStub queryStub;
    private final MerchantBusinessCommandServiceGrpc.MerchantBusinessCommandServiceBlockingStub commandStub;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponsePagination<List<MerchantBusinessResponse>>> findAll(
            @ModelAttribute FindAllMerchantRequest req) {
        var resp = queryStub.findAll(com.sanedge.ecommerce.pb.merchant.FindAllMerchantRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<MerchantBusinessResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantBusinesses(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<MerchantBusinessResponse>> findById(@PathVariable Integer id) {
        var resp = queryStub.findById(com.sanedge.ecommerce.pb.merchant_business.FindByIdMerchantBusinessRequest
                .newBuilder().setId(id).build());
        return ResponseEntity.ok(ApiResponse.<MerchantBusinessResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantBusiness(resp.getData()))
                .build());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponsePagination<List<MerchantBusinessResponseDeleteAt>>> findByActive(
            @ModelAttribute FindAllMerchantRequest req) {
        var resp = queryStub.findByActive(com.sanedge.ecommerce.pb.merchant.FindAllMerchantRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<MerchantBusinessResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantBusinessesDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponsePagination<List<MerchantBusinessResponseDeleteAt>>> findByTrashed(
            @ModelAttribute FindAllMerchantRequest req) {
        var resp = queryStub.findByTrashed(com.sanedge.ecommerce.pb.merchant.FindAllMerchantRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<MerchantBusinessResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantBusinessesDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantBusinessResponse>> create(
            @Valid @RequestBody CreateMerchantBusinessRequest req) {
        var resp = commandStub.create(com.sanedge.ecommerce.pb.merchant_business.CreateMerchantBusinessRequest
                .newBuilder()
                .setMerchantId(nvl(req.getMerchantId()))
                .setBusinessType(nvl(req.getBusinessType()))
                .setTaxId(nvl(req.getTaxId()))
                .setEstablishedYear(nvl(req.getEstablishedYear()))
                .setNumberOfEmployees(nvl(req.getNumberOfEmployees()))
                .setWebsiteUrl(nvl(req.getWebsiteUrl()))
                .build());
        return ResponseEntity.ok(ApiResponse.<MerchantBusinessResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantBusiness(resp.getData()))
                .build());
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantBusinessResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateMerchantBusinessRequest req) {
        req.setMerchantBusinessInfoId(id);
        var resp = commandStub.update(com.sanedge.ecommerce.pb.merchant_business.UpdateMerchantBusinessRequest
                .newBuilder()
                .setMerchantBusinessInfoId(nvl(req.getMerchantBusinessInfoId()))
                .setBusinessType(nvl(req.getBusinessType()))
                .setTaxId(nvl(req.getTaxId()))
                .setEstablishedYear(nvl(req.getEstablishedYear()))
                .setNumberOfEmployees(nvl(req.getNumberOfEmployees()))
                .setWebsiteUrl(nvl(req.getWebsiteUrl()))
                .build());
        return ResponseEntity.ok(ApiResponse.<MerchantBusinessResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantBusiness(resp.getData()))
                .build());
    }

    @PostMapping("/trashed/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantBusinessResponseDeleteAt>> trashed(@PathVariable Integer id) {
        var resp = commandStub.trashedMerchantBusiness(
                com.sanedge.ecommerce.pb.merchant_business.FindByIdMerchantBusinessRequest.newBuilder().setId(id)
                        .build());
        return ResponseEntity.ok(ApiResponse.<MerchantBusinessResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantBusinessDeleteAt(resp.getData()))
                .build());
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantBusinessResponseDeleteAt>> restore(@PathVariable Integer id) {
        var resp = commandStub.restoreMerchantBusiness(
                com.sanedge.ecommerce.pb.merchant_business.FindByIdMerchantBusinessRequest.newBuilder().setId(id)
                        .build());
        return ResponseEntity.ok(ApiResponse.<MerchantBusinessResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantBusinessDeleteAt(resp.getData()))
                .build());
    }

    @DeleteMapping("/permanent/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deletePermanent(@PathVariable Integer id) {
        var resp = commandStub.deleteMerchantBusinessPermanent(
                com.sanedge.ecommerce.pb.merchant_business.FindByIdMerchantBusinessRequest.newBuilder().setId(id)
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
        var resp = commandStub.restoreAllMerchantBusiness(Empty.getDefaultInstance());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/permanent/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllPermanent() {
        var resp = commandStub.deleteAllMerchantBusinessPermanent(Empty.getDefaultInstance());
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
