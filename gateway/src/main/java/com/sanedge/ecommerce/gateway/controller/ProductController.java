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
import com.sanedge.ecommerce.gateway.dto.requests.product.CreateProductRequest;
import com.sanedge.ecommerce.gateway.dto.requests.product.FindAllProductByCategoryRequest;
import com.sanedge.ecommerce.gateway.dto.requests.product.FindAllProductByMerchantRequest;
import com.sanedge.ecommerce.gateway.dto.requests.product.FindAllProductRequest;
import com.sanedge.ecommerce.gateway.dto.requests.product.UpdateProductRequest;
import com.sanedge.ecommerce.gateway.dto.responses.product.ProductResponse;
import com.sanedge.ecommerce.gateway.dto.responses.product.ProductResponseDeleteAt;
import com.sanedge.ecommerce.gateway.mapper.ProtoMapper;
import com.sanedge.ecommerce.gateway.service.FileService;
import com.sanedge.ecommerce.gateway.service.FolderService;
import com.sanedge.ecommerce.pb.product.ProductCommandServiceGrpc;
import com.sanedge.ecommerce.pb.product.ProductQueryServiceGrpc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {

    private final ProductQueryServiceGrpc.ProductQueryServiceBlockingStub productQueryStub;
    private final ProductCommandServiceGrpc.ProductCommandServiceBlockingStub productCommandStub;
    private final FileService fileService;
    private final FolderService folderService;

    private static final String PRODUCT_BASE_PATH = "static/product";

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponsePagination<List<ProductResponse>>> findAll(
            @ModelAttribute FindAllProductRequest req) {
        var resp = productQueryStub.findAll(com.sanedge.ecommerce.pb.product.FindAllProductRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<ProductResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.products(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<ProductResponse>> findById(@PathVariable Long id) {
        var resp = productQueryStub.findById(com.sanedge.ecommerce.pb.product.FindByIdProductRequest.newBuilder()
                .setId(id == null ? 0 : id.intValue())
                .build());
        return ResponseEntity.ok(ApiResponse.<ProductResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.product(resp.getData()))
                .build());
    }

    @GetMapping("/merchant/{merchant_id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponsePagination<List<ProductResponse>>> findByMerchant(
            @PathVariable("merchant_id") Integer merchantId,
            @ModelAttribute FindAllProductByMerchantRequest req) {
        var resp = productQueryStub.findByMerchant(
                com.sanedge.ecommerce.pb.product.FindAllProductMerchantRequest.newBuilder()
                        .setMerchantId(nvl(req.getMerchantId()))
                        .setCategoryId(nvl(req.getCategoryId()))
                        .setPage(nvl(req.getPage()))
                        .setPageSize(nvl(req.getPageSize()))
                        .setSearch(nvl(req.getSearch()))
                        .setMinPrice(nvl(req.getMinPrice()))
                        .setMaxPrice(nvl(req.getMaxPrice()))
                        .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<ProductResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.products(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/category/{category_name}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponsePagination<List<ProductResponse>>> findByCategory(
            @PathVariable("category_name") String categoryName,
            @ModelAttribute FindAllProductByCategoryRequest req) {
        var resp = productQueryStub.findByCategory(
                com.sanedge.ecommerce.pb.product.FindAllProductCategoryRequest.newBuilder()
                        .setCategoryName(nvl(req.getCategoryName()))
                        .setPage(nvl(req.getPage()))
                        .setPageSize(nvl(req.getPageSize()))
                        .setSearch(nvl(req.getSearch()))
                        .setMinPrice(nvl(req.getMinPrice()))
                        .setMaxPrice(nvl(req.getMaxPrice()))
                        .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<ProductResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.products(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<ProductResponseDeleteAt>>> findActive(
            @ModelAttribute FindAllProductRequest req) {
        var resp = productQueryStub.findByActive(com.sanedge.ecommerce.pb.product.FindAllProductRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<ProductResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.productsDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<ProductResponseDeleteAt>>> findTrashed(
            @ModelAttribute FindAllProductRequest req) {
        var resp = productQueryStub.findByTrashed(com.sanedge.ecommerce.pb.product.FindAllProductRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<ProductResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.productsDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @ModelAttribute CreateProductRequest req) {
        String image = saveImage(req.getSlugProduct(), req.getImageProduct());
        var resp = productCommandStub.create(com.sanedge.ecommerce.pb.product.CreateProductRequest.newBuilder()
                .setMerchantId(nvl(req.getMerchantId()))
                .setCategoryId(nvl(req.getCategoryId()))
                .setName(nvl(req.getName()))
                .setDescription(nvl(req.getDescription()))
                .setPrice(nvl(req.getPrice()))
                .setCountInStock(nvl(req.getCountInStock()))
                .setBrand(nvl(req.getBrand()))
                .setWeight(nvl(req.getWeight()))
                .setRating(nvl(req.getRating()))
                .setSlugProduct(nvl(req.getSlugProduct()))
                .setImageProduct(nvl(image))
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<ProductResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.product(resp.getData()))
                .build());
    }

    @PostMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Integer id,
            @Valid @ModelAttribute UpdateProductRequest req) {
        req.setProductId(id);
        String image = "";
        if (req.getImageProduct() != null && !req.getImageProduct().isEmpty()) {
            image = nvl(saveImage(req.getSlugProduct(), req.getImageProduct()));
        }
        var resp = productCommandStub.update(com.sanedge.ecommerce.pb.product.UpdateProductRequest.newBuilder()
                .setProductId(nvl(req.getProductId()))
                .setMerchantId(nvl(req.getMerchantId()))
                .setCategoryId(nvl(req.getCategoryId()))
                .setName(nvl(req.getName()))
                .setDescription(nvl(req.getDescription()))
                .setPrice(nvl(req.getPrice()))
                .setCountInStock(nvl(req.getCountInStock()))
                .setBrand(nvl(req.getBrand()))
                .setWeight(nvl(req.getWeight()))
                .setRating(nvl(req.getRating()))
                .setSlugProduct(nvl(req.getSlugProduct()))
                .setImageProduct(image)
                .build());
        return ResponseEntity.ok(ApiResponse.<ProductResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.product(resp.getData()))
                .build());
    }

    @PostMapping("/trashed/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponseDeleteAt>> trashedProduct(@PathVariable Integer id) {
        var resp = productCommandStub.trashedProduct(
                com.sanedge.ecommerce.pb.product.FindByIdProductRequest.newBuilder().setId(id).build());
        return ResponseEntity.ok(ApiResponse.<ProductResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.productDeleteAt(resp.getData()))
                .build());
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponseDeleteAt>> restoreProduct(@PathVariable Integer id) {
        var resp = productCommandStub.restoreProduct(
                com.sanedge.ecommerce.pb.product.FindByIdProductRequest.newBuilder().setId(id).build());
        return ResponseEntity.ok(ApiResponse.<ProductResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.productDeleteAt(resp.getData()))
                .build());
    }

    @DeleteMapping("/permanent/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteProductPermanent(@PathVariable Integer id) {
        var resp = productCommandStub.deleteProductPermanent(
                com.sanedge.ecommerce.pb.product.FindByIdProductRequest.newBuilder().setId(id).build());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/restore/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> restoreAllProducts() {
        var resp = productCommandStub.restoreAllProduct(Empty.getDefaultInstance());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/permanent/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllProductsPermanent() {
        var resp = productCommandStub.deleteAllProductPermanent(Empty.getDefaultInstance());
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
        String folderName = slug == null ? "product" : slug;
        String folderPath = folderService.createFolder(PRODUCT_BASE_PATH, folderName);
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
