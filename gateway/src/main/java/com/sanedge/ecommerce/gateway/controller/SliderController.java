package com.sanedge.ecommerce.gateway.controller;

import java.io.File;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.protobuf.Empty;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.gateway.dto.requests.slider.CreateSliderRequest;
import com.sanedge.ecommerce.gateway.dto.requests.slider.FindAllSliderRequest;
import com.sanedge.ecommerce.gateway.dto.requests.slider.UpdateSliderRequest;
import com.sanedge.ecommerce.gateway.dto.responses.slider.SliderResponse;
import com.sanedge.ecommerce.gateway.dto.responses.slider.SliderResponseDeleteAt;
import com.sanedge.ecommerce.gateway.mapper.ProtoMapper;
import com.sanedge.ecommerce.gateway.service.FileService;
import com.sanedge.ecommerce.gateway.service.FolderService;
import com.sanedge.ecommerce.pb.slider.SliderCommandServiceGrpc;
import com.sanedge.ecommerce.pb.slider.SliderQueryServiceGrpc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/slider")
public class SliderController {

    private final SliderQueryServiceGrpc.SliderQueryServiceBlockingStub sliderQueryStub;
    private final SliderCommandServiceGrpc.SliderCommandServiceBlockingStub sliderCommandStub;
    private final FileService fileService;
    private final FolderService folderService;

    private static final String SLIDER_BASE_PATH = "static/slider";

    @GetMapping("")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponsePagination<List<SliderResponse>>> findAll(
            @Valid FindAllSliderRequest req) {
        var resp = sliderQueryStub.findAll(com.sanedge.ecommerce.pb.slider.FindAllSliderRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<SliderResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.sliders(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<SliderResponseDeleteAt>>> findByActive(
            @Valid FindAllSliderRequest req) {
        var resp = sliderQueryStub.findByActive(com.sanedge.ecommerce.pb.slider.FindAllSliderRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<SliderResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.slidersDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<SliderResponseDeleteAt>>> findByTrashed(
            @Valid FindAllSliderRequest req) {
        var resp = sliderQueryStub.findByTrashed(com.sanedge.ecommerce.pb.slider.FindAllSliderRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<SliderResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.slidersDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<SliderResponse>> create(@Valid @ModelAttribute CreateSliderRequest req) {
        String image = saveImage(req.getNama(), req.getFilePath());
        var resp = sliderCommandStub.create(com.sanedge.ecommerce.pb.slider.CreateSliderRequest.newBuilder()
                .setName(nvl(req.getNama()))
                .setImage(nvl(image))
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<SliderResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.slider(resp.getData()))
                .build());
    }

    @PostMapping(value = "/update/{sliderId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<SliderResponse>> update(
            @PathVariable Integer sliderId,
            @Valid @ModelAttribute UpdateSliderRequest req) {
        req.setId(sliderId);
        String image = "";
        if (req.getFilePath() != null && !req.getFilePath().isEmpty()) {
            image = nvl(saveImage(req.getNama(), req.getFilePath()));
        }
        var resp = sliderCommandStub.update(com.sanedge.ecommerce.pb.slider.UpdateSliderRequest.newBuilder()
                .setId(nvl(req.getId()))
                .setName(nvl(req.getNama()))
                .setImage(image)
                .build());
        return ResponseEntity.ok(ApiResponse.<SliderResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.slider(resp.getData()))
                .build());
    }

    @PostMapping("/trashed/{sliderId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<SliderResponseDeleteAt>> trash(@PathVariable Integer sliderId) {
        var resp = sliderCommandStub.trashedSlider(
                com.sanedge.ecommerce.pb.slider.FindByIdSliderRequest.newBuilder().setId(sliderId).build());
        return ResponseEntity.ok(ApiResponse.<SliderResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.sliderDeleteAt(resp.getData()))
                .build());
    }

    @PostMapping("/restore/{sliderId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<SliderResponseDeleteAt>> restore(@PathVariable Integer sliderId) {
        var resp = sliderCommandStub.restoreSlider(
                com.sanedge.ecommerce.pb.slider.FindByIdSliderRequest.newBuilder().setId(sliderId).build());
        return ResponseEntity.ok(ApiResponse.<SliderResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.sliderDeleteAt(resp.getData()))
                .build());
    }

    @DeleteMapping("/permanent/{sliderId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deletePermanent(@PathVariable Integer sliderId) {
        var resp = sliderCommandStub.deleteSliderPermanent(
                com.sanedge.ecommerce.pb.slider.FindByIdSliderRequest.newBuilder().setId(sliderId).build());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/restore/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> restoreAll() {
        var resp = sliderCommandStub.restoreAllSlider(Empty.getDefaultInstance());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/permanent/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllPermanent() {
        var resp = sliderCommandStub.deleteAllSliderPermanent(Empty.getDefaultInstance());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    private String saveImage(String name, org.springframework.web.multipart.MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String folderName = name == null ? "slider" : name.replace(" ", "_");
        String folderPath = folderService.createFolder(SLIDER_BASE_PATH, folderName);
        if (folderPath == null) {
            return null;
        }
        String filePath = folderPath + File.separator + file.getOriginalFilename();
        return fileService.createFileImage(file, filePath);
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
