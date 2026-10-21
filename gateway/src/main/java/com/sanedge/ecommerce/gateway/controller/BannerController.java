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
import com.sanedge.ecommerce.gateway.dto.requests.banner.CreateBannerRequest;
import com.sanedge.ecommerce.gateway.dto.requests.banner.FindAllBannerRequest;
import com.sanedge.ecommerce.gateway.dto.requests.banner.UpdateBannerRequest;
import com.sanedge.ecommerce.gateway.dto.responses.banner.BannerResponse;
import com.sanedge.ecommerce.gateway.dto.responses.banner.BannerResponseDeleteAt;
import com.sanedge.ecommerce.gateway.mapper.ProtoMapper;
import com.sanedge.ecommerce.pb.banner.BannerCommandServiceGrpc;
import com.sanedge.ecommerce.pb.banner.BannerQueryServiceGrpc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/banner")
public class BannerController {

    private final BannerQueryServiceGrpc.BannerQueryServiceBlockingStub bannerQueryStub;
    private final BannerCommandServiceGrpc.BannerCommandServiceBlockingStub bannerCommandStub;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<BannerResponse>>> findAll(
            @ModelAttribute FindAllBannerRequest req) {
        var resp = bannerQueryStub.findAll(com.sanedge.ecommerce.pb.banner.FindAllBannerRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<BannerResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.banners(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<BannerResponseDeleteAt>>> findByActive(
            @ModelAttribute FindAllBannerRequest req) {
        var resp = bannerQueryStub.findByActive(com.sanedge.ecommerce.pb.banner.FindAllBannerRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<BannerResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.bannersDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<BannerResponseDeleteAt>>> findByTrashed(
            @ModelAttribute FindAllBannerRequest req) {
        var resp = bannerQueryStub.findByTrashed(com.sanedge.ecommerce.pb.banner.FindAllBannerRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<BannerResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.bannersDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<BannerResponse>> findById(@PathVariable Integer id) {
        var resp = bannerQueryStub.findById(com.sanedge.ecommerce.pb.banner.FindByIdBannerRequest.newBuilder()
                .setId(id)
                .build());
        return ResponseEntity.ok(ApiResponse.<BannerResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.banner(resp.getData()))
                .build());
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<BannerResponse>> createBanner(@Valid @RequestBody CreateBannerRequest req) {
        var resp = bannerCommandStub.create(com.sanedge.ecommerce.pb.banner.CreateBannerRequest.newBuilder()
                .setName(nvl(req.getName()))
                .setStartDate(nvl(req.getStartDate()))
                .setEndDate(nvl(req.getEndDate()))
                .setStartTime(nvl(req.getStartTime()))
                .setEndTime(nvl(req.getEndTime()))
                .setIsActive(Boolean.TRUE.equals(req.getIsActive()))
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<BannerResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.banner(resp.getData()))
                .build());
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<BannerResponse>> updateBanner(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateBannerRequest req) {
        req.setBannerID(id);
        var resp = bannerCommandStub.update(com.sanedge.ecommerce.pb.banner.UpdateBannerRequest.newBuilder()
                .setBannerId(nvl(req.getBannerID()))
                .setName(nvl(req.getName()))
                .setStartDate(nvl(req.getStartDate()))
                .setEndDate(nvl(req.getEndDate()))
                .setStartTime(nvl(req.getStartTime()))
                .setEndTime(nvl(req.getEndTime()))
                .setIsActive(Boolean.TRUE.equals(req.getIsActive()))
                .build());
        return ResponseEntity.ok(ApiResponse.<BannerResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.banner(resp.getData()))
                .build());
    }

    @PostMapping("/trashed/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<BannerResponseDeleteAt>> trashedBanner(@PathVariable Integer id) {
        var resp = bannerCommandStub.trash(
                com.sanedge.ecommerce.pb.banner.FindByIdBannerRequest.newBuilder().setId(id).build());
        return ResponseEntity.ok(ApiResponse.<BannerResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.bannerDeleteAt(resp.getData()))
                .build());
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<BannerResponseDeleteAt>> restoreBanner(@PathVariable Integer id) {
        var resp = bannerCommandStub.restore(
                com.sanedge.ecommerce.pb.banner.FindByIdBannerRequest.newBuilder().setId(id).build());
        return ResponseEntity.ok(ApiResponse.<BannerResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.bannerDeleteAt(resp.getData()))
                .build());
    }

    @DeleteMapping("/permanent/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteBannerPermanent(@PathVariable Integer id) {
        var resp = bannerCommandStub.deletePermanent(
                com.sanedge.ecommerce.pb.banner.FindByIdBannerRequest.newBuilder().setId(id).build());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/restore/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> restoreAllBanner() {
        var resp = bannerCommandStub.restoreAll(Empty.getDefaultInstance());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/permanent/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllBannerPermanent() {
        var resp = bannerCommandStub.deleteAll(Empty.getDefaultInstance());
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
