package com.sanedge.ecommerce.gateway.controller;

import java.io.File;
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
import com.sanedge.ecommerce.gateway.dto.requests.merchantdetail.CreateMerchantDetailRequest;
import com.sanedge.ecommerce.gateway.dto.requests.merchantdetail.UpdateMerchantDetailRequest;
import com.sanedge.ecommerce.gateway.dto.responses.merchantdetail.MerchantDetailRelationResponse;
import com.sanedge.ecommerce.gateway.dto.responses.merchantdetail.MerchantDetailRelationResponseDeleteAt;
import com.sanedge.ecommerce.gateway.dto.responses.merchantdetail.MerchantDetailResponse;
import com.sanedge.ecommerce.gateway.dto.responses.merchantdetail.MerchantDetailResponseDeleteAt;
import com.sanedge.ecommerce.gateway.mapper.ProtoMapper;
import com.sanedge.ecommerce.gateway.service.FileService;
import com.sanedge.ecommerce.gateway.service.FolderService;
import com.sanedge.ecommerce.pb.merchant_detail.MerchantDetailCommandServiceGrpc;
import com.sanedge.ecommerce.pb.merchant_detail.MerchantDetailQueryServiceGrpc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/merchant-detail")
public class MerchantDetailController {

    private final MerchantDetailQueryServiceGrpc.MerchantDetailQueryServiceBlockingStub queryStub;
    private final MerchantDetailCommandServiceGrpc.MerchantDetailCommandServiceBlockingStub commandStub;
    private final FileService fileService;
    private final FolderService folderService;

    private static final String MERCHANT_BASE_PATH = "static/merchant-detail";

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponsePagination<List<MerchantDetailRelationResponse>>> findAll(
            @ModelAttribute FindAllMerchantRequest req) {
        var resp = queryStub.findAll(com.sanedge.ecommerce.pb.merchant.FindAllMerchantRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<MerchantDetailRelationResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantDetailRelations(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<MerchantDetailRelationResponse>> findById(@PathVariable Integer id) {
        var resp = queryStub.findById(com.sanedge.ecommerce.pb.merchant_detail.FindByIdMerchantDetailRequest
                .newBuilder().setId(id).build());
        return ResponseEntity.ok(ApiResponse.<MerchantDetailRelationResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantDetailRelation(resp.getData()))
                .build());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<MerchantDetailRelationResponseDeleteAt>>> findByActive(
            @ModelAttribute FindAllMerchantRequest req) {
        var resp = queryStub.findByActive(com.sanedge.ecommerce.pb.merchant.FindAllMerchantRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<MerchantDetailRelationResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantDetailRelationsDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<MerchantDetailRelationResponseDeleteAt>>> findByTrashed(
            @ModelAttribute FindAllMerchantRequest req) {
        var resp = queryStub.findByTrashed(com.sanedge.ecommerce.pb.merchant.FindAllMerchantRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<MerchantDetailRelationResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantDetailRelationsDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantDetailResponse>> createMerchant(
            @Valid @RequestBody CreateMerchantDetailRequest req) {
        String folderPath = folderService.createFolder(MERCHANT_BASE_PATH, "merchant_" + nvl(req.getMerchantId()));
        String cover = null;
        String logo = null;
        if (folderPath != null && req.getCoverImageUrl() != null && !req.getCoverImageUrl().isEmpty()) {
            cover = fileService.createFileImage(req.getCoverImageUrl(),
                    folderPath + File.separator + req.getCoverImageUrl().getOriginalFilename());
        }
        if (folderPath != null && req.getLogoUrl() != null && !req.getLogoUrl().isEmpty()) {
            logo = fileService.createFileImage(req.getLogoUrl(),
                    folderPath + File.separator + req.getLogoUrl().getOriginalFilename());
        }
        var resp = commandStub.create(com.sanedge.ecommerce.pb.merchant_detail.CreateMerchantDetailRequest.newBuilder()
                .setMerchantId(nvl(req.getMerchantId()))
                .setDisplayName(nvl(req.getDisplayName()))
                .setCoverImageUrl(nvl(cover))
                .setLogoUrl(nvl(logo))
                .setShortDescription(nvl(req.getShortDescription()))
                .setWebsiteUrl(nvl(req.getWebsiteUrl()))
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<MerchantDetailResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantDetail(resp.getData()))
                .build());
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantDetailResponse>> updateMerchant(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateMerchantDetailRequest req) {
        req.setMerchantDetailId(id);
        String folderPath = folderService.createFolder(MERCHANT_BASE_PATH, "merchant_" + nvl(req.getMerchantDetailId()));
        String cover = "";
        String logo = "";
        if (folderPath != null && req.getCoverImageUrl() != null && !req.getCoverImageUrl().isEmpty()) {
            cover = nvl(fileService.createFileImage(req.getCoverImageUrl(),
                    folderPath + File.separator + req.getCoverImageUrl().getOriginalFilename()));
        }
        if (folderPath != null && req.getLogoUrl() != null && !req.getLogoUrl().isEmpty()) {
            logo = nvl(fileService.createFileImage(req.getLogoUrl(),
                    folderPath + File.separator + req.getLogoUrl().getOriginalFilename()));
        }
        var resp = commandStub.update(com.sanedge.ecommerce.pb.merchant_detail.UpdateMerchantDetailRequest.newBuilder()
                .setMerchantDetailId(nvl(req.getMerchantDetailId()))
                .setDisplayName(nvl(req.getDisplayName()))
                .setCoverImageUrl(cover)
                .setLogoUrl(logo)
                .setShortDescription(nvl(req.getShortDescription()))
                .setWebsiteUrl(nvl(req.getWebsiteUrl()))
                .build());
        return ResponseEntity.ok(ApiResponse.<MerchantDetailResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantDetail(resp.getData()))
                .build());
    }

    @PostMapping("/trashed/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantDetailResponseDeleteAt>> trashedMerchant(@PathVariable Integer id) {
        var resp = commandStub.trashedMerchantDetail(
                com.sanedge.ecommerce.pb.merchant_detail.FindByIdMerchantDetailRequest.newBuilder().setId(id).build());
        return ResponseEntity.ok(ApiResponse.<MerchantDetailResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantDetailDeleteAt(resp.getData()))
                .build());
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantDetailResponseDeleteAt>> restoreMerchant(@PathVariable Integer id) {
        var resp = commandStub.restoreMerchantDetail(
                com.sanedge.ecommerce.pb.merchant_detail.FindByIdMerchantDetailRequest.newBuilder().setId(id).build());
        return ResponseEntity.ok(ApiResponse.<MerchantDetailResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantDetailDeleteAt(resp.getData()))
                .build());
    }

    @DeleteMapping("/permanent/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteMerchantPermanent(@PathVariable Integer id) {
        var resp = commandStub.deleteMerchantDetailPermanent(
                com.sanedge.ecommerce.pb.merchant_detail.FindByIdMerchantDetailRequest.newBuilder().setId(id).build());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/restore/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> restoreAllMerchant() {
        var resp = commandStub.restoreAllMerchantDetail(Empty.getDefaultInstance());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/permanent/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllMerchantPermanent() {
        var resp = commandStub.deleteAllMerchantDetailPermanent(Empty.getDefaultInstance());
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
