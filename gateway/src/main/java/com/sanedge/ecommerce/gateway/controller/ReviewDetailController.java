package com.sanedge.ecommerce.gateway.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.protobuf.Empty;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.gateway.dto.requests.reviewdetail.CreateReviewDetailRequest;
import com.sanedge.ecommerce.gateway.dto.requests.reviewdetail.UpdateReviewDetailRequest;
import com.sanedge.ecommerce.gateway.dto.responses.reviewdetail.ReviewDetailResponse;
import com.sanedge.ecommerce.gateway.dto.responses.reviewdetail.ReviewDetailResponseDeleteAt;
import com.sanedge.ecommerce.gateway.mapper.ProtoMapper;
import com.sanedge.ecommerce.gateway.service.FileService;
import com.sanedge.ecommerce.gateway.service.FolderService;
import com.sanedge.ecommerce.pb.review_detail.ReviewDetailCommandServiceGrpc;
import com.sanedge.ecommerce.pb.review_detail.ReviewDetailQueryServiceGrpc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review-details")
public class ReviewDetailController {

    private static final String REVIEW_DETAIL_BASE_PATH = "static/review_detail";

    private final ReviewDetailQueryServiceGrpc.ReviewDetailQueryServiceBlockingStub reviewDetailQueryStub;
    private final ReviewDetailCommandServiceGrpc.ReviewDetailCommandServiceBlockingStub reviewDetailCommandStub;
    private final FileService fileService;
    private final FolderService folderService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ReviewDetailResponse>>> create(
            @Valid @ModelAttribute List<CreateReviewDetailRequest> request) {
        List<ReviewDetailResponse> responses = new ArrayList<>();
        String status = "error";
        String message = "Failed to create any review detail";

        for (CreateReviewDetailRequest req : request) {
            String savedPath = saveReviewDetailFile(req.getReviewId(), req.getFile());
            if (savedPath == null) {
                continue;
            }
            var resp = reviewDetailCommandStub.create(
                    com.sanedge.ecommerce.pb.review_detail.CreateReviewDetailRequest.newBuilder()
                            .setReviewId(nvl(req.getReviewId()))
                            .setType(nvl(req.getType()))
                            .setUrl(nvl(savedPath))
                            .setCaption(nvl(req.getCaption()))
                            .build());
            status = resp.getStatus();
            message = resp.getMessage();
            responses.add(ProtoMapper.reviewDetail(resp.getData()));
        }

        if (responses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<List<ReviewDetailResponse>>builder()
                    .status(status)
                    .message(message)
                    .data(null)
                    .build());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<List<ReviewDetailResponse>>builder()
                .status(status)
                .message(message)
                .data(responses)
                .build());
    }

    @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ReviewDetailResponse>>> update(
            @Valid @ModelAttribute List<UpdateReviewDetailRequest> request) {
        List<ReviewDetailResponse> responses = new ArrayList<>();
        String status = "error";
        String message = "Failed to update any review detail";

        for (UpdateReviewDetailRequest req : request) {
            var existing = reviewDetailQueryStub.findById(
                    com.sanedge.ecommerce.pb.review_detail.FindByIdReviewDetailRequest.newBuilder()
                            .setId(nvl(req.getReviewDetailId()))
                            .build());
            Integer folderId = existing.hasData() ? existing.getData().getReviewId() : req.getReviewDetailId();
            String savedPath = saveReviewDetailFile(folderId, req.getFile());
            if (savedPath == null) {
                continue;
            }
            var resp = reviewDetailCommandStub.update(
                    com.sanedge.ecommerce.pb.review_detail.UpdateReviewDetailRequest.newBuilder()
                            .setReviewDetailId(nvl(req.getReviewDetailId()))
                            .setType(nvl(req.getType()))
                            .setUrl(nvl(savedPath))
                            .setCaption(nvl(req.getCaption()))
                            .build());
            status = resp.getStatus();
            message = resp.getMessage();
            responses.add(ProtoMapper.reviewDetail(resp.getData()));
        }

        if (responses.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.<List<ReviewDetailResponse>>builder()
                    .status(status)
                    .message(message)
                    .data(null)
                    .build());
        }
        return ResponseEntity.ok(ApiResponse.<List<ReviewDetailResponse>>builder()
                .status(status)
                .message(message)
                .data(responses)
                .build());
    }

    @PostMapping("/trashed/{reviewDetailId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReviewDetailResponseDeleteAt>> trash(
            @PathVariable Integer reviewDetailId) {
        var resp = reviewDetailCommandStub.trashedReviewDetail(
                com.sanedge.ecommerce.pb.review_detail.FindByIdReviewDetailRequest.newBuilder()
                        .setId(reviewDetailId)
                        .build());
        return ResponseEntity.ok(ApiResponse.<ReviewDetailResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.reviewDetailDeleteAt(resp.getData()))
                .build());
    }

    @PatchMapping("/restore/{reviewDetailId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReviewDetailResponseDeleteAt>> restore(
            @PathVariable Integer reviewDetailId) {
        var resp = reviewDetailCommandStub.restoreReviewDetail(
                com.sanedge.ecommerce.pb.review_detail.FindByIdReviewDetailRequest.newBuilder()
                        .setId(reviewDetailId)
                        .build());
        return ResponseEntity.ok(ApiResponse.<ReviewDetailResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.reviewDetailDeleteAt(resp.getData()))
                .build());
    }

    @DeleteMapping("/permanent/{reviewDetailId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> delete(@PathVariable Integer reviewDetailId) {
        var resp = reviewDetailCommandStub.deleteReviewDetailPermanent(
                com.sanedge.ecommerce.pb.review_detail.FindByIdReviewDetailRequest.newBuilder()
                        .setId(reviewDetailId)
                        .build());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PatchMapping("/restore-all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> restoreAll() {
        var resp = reviewDetailCommandStub.restoreAllReviewDetail(Empty.getDefaultInstance());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @DeleteMapping("/delete-all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAll() {
        var resp = reviewDetailCommandStub.deleteAllReviewDetailPermanent(Empty.getDefaultInstance());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    private String saveReviewDetailFile(Integer reviewId, org.springframework.web.multipart.MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String folderName = reviewId == null ? "review_detail" : reviewId.toString();
        String folderPath = folderService.createFolder(REVIEW_DETAIL_BASE_PATH, folderName);
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
