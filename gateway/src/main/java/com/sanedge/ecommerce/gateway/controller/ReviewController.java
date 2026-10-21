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
import com.sanedge.ecommerce.gateway.dto.requests.review.CreateReviewRequest;
import com.sanedge.ecommerce.gateway.dto.requests.review.FindAllReview;
import com.sanedge.ecommerce.gateway.dto.requests.review.FindAllReviewByMerchant;
import com.sanedge.ecommerce.gateway.dto.requests.review.FindAllReviewByProduct;
import com.sanedge.ecommerce.gateway.dto.requests.review.UpdateReviewRequest;
import com.sanedge.ecommerce.gateway.dto.responses.reviews.ReviewRelationsDetailResponse;
import com.sanedge.ecommerce.gateway.dto.responses.reviews.ReviewResponse;
import com.sanedge.ecommerce.gateway.dto.responses.reviews.ReviewResponseDeleteAt;
import com.sanedge.ecommerce.gateway.mapper.ProtoMapper;
import com.sanedge.ecommerce.pb.review.ReviewCommandServiceGrpc;
import com.sanedge.ecommerce.pb.review.ReviewQueryServiceGrpc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
public class ReviewController {

    private final ReviewQueryServiceGrpc.ReviewQueryServiceBlockingStub reviewQueryStub;
    private final ReviewCommandServiceGrpc.ReviewCommandServiceBlockingStub reviewCommandStub;

    @GetMapping("")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponsePagination<List<ReviewResponse>>> findAll(
            @ModelAttribute FindAllReview req) {
        var resp = reviewQueryStub.findAll(com.sanedge.ecommerce.pb.review.FindAllReviewRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<ReviewResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.reviews(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponsePagination<List<ReviewResponseDeleteAt>>> findActive(
            @ModelAttribute FindAllReview req) {
        var resp = reviewQueryStub.findByActive(com.sanedge.ecommerce.pb.review.FindAllReviewRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<ReviewResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.reviewsDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponsePagination<List<ReviewResponseDeleteAt>>> findTrashed(
            @ModelAttribute FindAllReview req) {
        var resp = reviewQueryStub.findByTrashed(com.sanedge.ecommerce.pb.review.FindAllReviewRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<ReviewResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.reviewsDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponsePagination<List<ReviewRelationsDetailResponse>>> findByProduct(
            @PathVariable Integer productId,
            @ModelAttribute FindAllReviewByProduct req) {
        req.setProductId(productId);
        var resp = reviewQueryStub.findByProduct(
                com.sanedge.ecommerce.pb.review.FindAllReviewProductRequest.newBuilder()
                        .setProductId(nvl(req.getProductId()))
                        .setPage(nvl(req.getPage()))
                        .setPageSize(nvl(req.getPageSize()))
                        .setSearch(nvl(req.getSearch()))
                        .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<ReviewRelationsDetailResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.reviewRelationsDetails(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/merchant/{merchantId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponsePagination<List<ReviewRelationsDetailResponse>>> findByMerchant(
            @PathVariable Integer merchantId,
            @ModelAttribute FindAllReviewByMerchant req) {
        req.setMerchantId(merchantId);
        var resp = reviewQueryStub.findByMerchant(
                com.sanedge.ecommerce.pb.review.FindAllReviewMerchantRequest.newBuilder()
                        .setMerchantId(nvl(req.getMerchantId()))
                        .setPage(nvl(req.getPage()))
                        .setPageSize(nvl(req.getPageSize()))
                        .setSearch(nvl(req.getSearch()))
                        .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<ReviewRelationsDetailResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.reviewRelationsDetails(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/{reviewId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<ReviewResponse>> findById(@PathVariable Integer reviewId) {
        var resp = reviewQueryStub.findById(
                com.sanedge.ecommerce.pb.review.FindByIdReviewRequest.newBuilder().setId(reviewId).build());
        return ResponseEntity.ok(ApiResponse.<ReviewResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.review(resp.getData()))
                .build());
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReviewResponse>> create(@Valid @RequestBody CreateReviewRequest request) {
        var resp = reviewCommandStub.create(com.sanedge.ecommerce.pb.review.CreateReviewRequest.newBuilder()
                .setUserId(request.getUserId())
                .setProductId(request.getProductId())
                .setName("")
                .setComment(nvl(request.getComment()))
                .setRating(request.getRating())
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<ReviewResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.review(resp.getData()))
                .build());
    }

    @PostMapping("/update/{reviewId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReviewResponse>> update(
            @PathVariable Integer reviewId,
            @Valid @RequestBody UpdateReviewRequest request) {
        request.setReviewId(reviewId);
        var resp = reviewCommandStub.update(com.sanedge.ecommerce.pb.review.UpdateReviewRequest.newBuilder()
                .setReviewId(nvl(request.getReviewId()))
                .setName(nvl(request.getName()))
                .setComment(nvl(request.getComment()))
                .setRating(request.getRating())
                .build());
        return ResponseEntity.ok(ApiResponse.<ReviewResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.review(resp.getData()))
                .build());
    }

    @PostMapping("/trashed/{reviewId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReviewResponseDeleteAt>> trash(@PathVariable Integer reviewId) {
        var resp = reviewCommandStub.trashedReview(
                com.sanedge.ecommerce.pb.review.FindByIdReviewRequest.newBuilder().setId(reviewId).build());
        return ResponseEntity.ok(ApiResponse.<ReviewResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.reviewDeleteAt(resp.getData()))
                .build());
    }

    @PostMapping("/restore/{reviewId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReviewResponseDeleteAt>> restore(@PathVariable Integer reviewId) {
        var resp = reviewCommandStub.restoreReview(
                com.sanedge.ecommerce.pb.review.FindByIdReviewRequest.newBuilder().setId(reviewId).build());
        return ResponseEntity.ok(ApiResponse.<ReviewResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.reviewDeleteAt(resp.getData()))
                .build());
    }

    @DeleteMapping("/permanent/{reviewId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deletePermanent(@PathVariable Integer reviewId) {
        var resp = reviewCommandStub.deleteReviewPermanent(
                com.sanedge.ecommerce.pb.review.FindByIdReviewRequest.newBuilder().setId(reviewId).build());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/restore/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> restoreAll() {
        var resp = reviewCommandStub.restoreAllReview(Empty.getDefaultInstance());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/permanent/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllPermanent() {
        var resp = reviewCommandStub.deleteAllReviewPermanent(Empty.getDefaultInstance());
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
