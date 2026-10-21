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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.protobuf.Empty;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.gateway.dto.requests.category.CreateCategoryRequest;
import com.sanedge.ecommerce.gateway.dto.requests.category.FindAllCategoryRequest;
import com.sanedge.ecommerce.gateway.dto.requests.category.MonthPriceIdRequest;
import com.sanedge.ecommerce.gateway.dto.requests.category.MonthPriceMerchantRequest;
import com.sanedge.ecommerce.gateway.dto.requests.category.MonthTotalPriceIdRequest;
import com.sanedge.ecommerce.gateway.dto.requests.category.MonthTotalPriceMerchantRequest;
import com.sanedge.ecommerce.gateway.dto.requests.category.MonthTotalPriceRequest;
import com.sanedge.ecommerce.gateway.dto.requests.category.UpdateCategoryRequest;
import com.sanedge.ecommerce.gateway.dto.requests.category.YearPriceIdRequest;
import com.sanedge.ecommerce.gateway.dto.requests.category.YearPriceMerchantRequest;
import com.sanedge.ecommerce.gateway.dto.requests.category.YearTotalPriceIdRequest;
import com.sanedge.ecommerce.gateway.dto.requests.category.YearTotalPriceMerchantRequest;
import com.sanedge.ecommerce.gateway.dto.responses.category.CategoriesMonthlyTotalPriceResponse;
import com.sanedge.ecommerce.gateway.dto.responses.category.CategoriesMonthPriceResponse;
import com.sanedge.ecommerce.gateway.dto.responses.category.CategoriesYearlyTotalPriceResponse;
import com.sanedge.ecommerce.gateway.dto.responses.category.CategoriesYearPriceResponse;
import com.sanedge.ecommerce.gateway.dto.responses.category.CategoryResponse;
import com.sanedge.ecommerce.gateway.dto.responses.category.CategoryResponseDeleteAt;
import com.sanedge.ecommerce.gateway.mapper.ProtoMapper;
import com.sanedge.ecommerce.gateway.service.FileService;
import com.sanedge.ecommerce.gateway.service.FolderService;
import com.sanedge.ecommerce.pb.category.CategoryCommandServiceGrpc;
import com.sanedge.ecommerce.pb.category.CategoryQueryServiceGrpc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryQueryServiceGrpc.CategoryQueryServiceBlockingStub categoryQueryStub;
    private final CategoryCommandServiceGrpc.CategoryCommandServiceBlockingStub categoryCommandStub;
    private final FileService fileService;
    private final FolderService folderService;

    private static final String CATEGORY_BASE_PATH = "static/category";

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponsePagination<List<CategoryResponse>>> findAll(
            @ModelAttribute FindAllCategoryRequest req) {
        var resp = categoryQueryStub.findAll(com.sanedge.ecommerce.pb.category.FindAllCategoryRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<CategoryResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.categories(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponsePagination<List<CategoryResponseDeleteAt>>> findByActive(
            @ModelAttribute FindAllCategoryRequest req) {
        var resp = categoryQueryStub.findByActive(com.sanedge.ecommerce.pb.category.FindAllCategoryRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<CategoryResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.categoriesDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponsePagination<List<CategoryResponseDeleteAt>>> findByTrashed(
            @ModelAttribute FindAllCategoryRequest req) {
        var resp = categoryQueryStub.findByTrashed(com.sanedge.ecommerce.pb.category.FindAllCategoryRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<CategoryResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.categoriesDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<CategoryResponse>> findById(@PathVariable Integer id) {
        var resp = categoryQueryStub.findById(com.sanedge.ecommerce.pb.category.FindByIdCategoryRequest.newBuilder()
                .setId(id)
                .build());
        return ResponseEntity.ok(ApiResponse.<CategoryResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.category(resp.getData()))
                .build());
    }


    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @ModelAttribute CreateCategoryRequest req) {
        String image = saveImage(req.getSlugCategory(), req.getImageCategory());
        var resp = categoryCommandStub.create(com.sanedge.ecommerce.pb.category.CreateCategoryRequest.newBuilder()
                .setName(nvl(req.getName()))
                .setDescription(nvl(req.getDescription()))
                .setSlugCategory(nvl(req.getSlugCategory()))
                .setImageCategory(nvl(image))
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<CategoryResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.category(resp.getData()))
                .build());
    }

    @PostMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Integer id,
            @Valid @ModelAttribute UpdateCategoryRequest req) {
        req.setCategoryId(id);
        String image = "";
        if (req.getImageCategory() != null && !req.getImageCategory().isEmpty()) {
            image = nvl(saveImage(req.getSlugCategory(), req.getImageCategory()));
        }
        var resp = categoryCommandStub.update(com.sanedge.ecommerce.pb.category.UpdateCategoryRequest.newBuilder()
                .setCategoryId(nvl(req.getCategoryId()))
                .setName(nvl(req.getName()))
                .setDescription(nvl(req.getDescription()))
                .setSlugCategory(nvl(req.getSlugCategory()))
                .setImageCategory(image)
                .build());
        return ResponseEntity.ok(ApiResponse.<CategoryResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.category(resp.getData()))
                .build());
    }

    @PostMapping("/trashed/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponseDeleteAt>> trashedCategory(@PathVariable Integer id) {
        var resp = categoryCommandStub.trashedCategory(
                com.sanedge.ecommerce.pb.category.FindByIdCategoryRequest.newBuilder().setId(id).build());
        return ResponseEntity.ok(ApiResponse.<CategoryResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.categoryDeleteAt(resp.getData()))
                .build());
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponseDeleteAt>> restoreCategory(@PathVariable Integer id) {
        var resp = categoryCommandStub.restoreCategory(
                com.sanedge.ecommerce.pb.category.FindByIdCategoryRequest.newBuilder().setId(id).build());
        return ResponseEntity.ok(ApiResponse.<CategoryResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.categoryDeleteAt(resp.getData()))
                .build());
    }

    @DeleteMapping("/permanent/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteCategoryPermanent(@PathVariable Integer id) {
        var resp = categoryCommandStub.deleteCategoryPermanent(
                com.sanedge.ecommerce.pb.category.FindByIdCategoryRequest.newBuilder().setId(id).build());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/restore/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> restoreAllCategories() {
        var resp = categoryCommandStub.restoreAllCategory(Empty.getDefaultInstance());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/permanent/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllCategoriesPermanent() {
        var resp = categoryCommandStub.deleteAllCategoryPermanent(Empty.getDefaultInstance());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    private String saveImage(String slug, org.springframework.web.multipart.MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String folderName = slug == null ? "category" : slug;
        String folderPath = folderService.createFolder(CATEGORY_BASE_PATH, folderName);
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
