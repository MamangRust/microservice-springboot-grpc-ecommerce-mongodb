package com.sanedge.ecommerce.gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.gateway.dto.requests.merchantsociallink.CreateMerchantSocialRequest;
import com.sanedge.ecommerce.gateway.dto.requests.merchantsociallink.UpdateMerchantSocialRequest;
import com.sanedge.ecommerce.gateway.dto.responses.merchantsociallink.MerchantSocialMediaLinkResponse;
import com.sanedge.ecommerce.gateway.dto.responses.merchantsociallink.MerchantSocialMediaLinkResponseDeleteAt;
import com.sanedge.ecommerce.gateway.mapper.ProtoMapper;
import com.sanedge.ecommerce.pb.merchant_social_link.MerchantSocialCommandServiceGrpc;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/merchant-social-link")
public class MerchantSocialLinkController {

    private final MerchantSocialCommandServiceGrpc.MerchantSocialCommandServiceBlockingStub commandStub;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantSocialMediaLinkResponse>> create(
            @RequestBody CreateMerchantSocialRequest request) {
        var resp = commandStub.create(
                com.sanedge.ecommerce.pb.merchant_social_link.CreateMerchantSocialRequest.newBuilder()
                        .setMerchantDetailId(nvl(request.getMerchantDetailId()))
                        .setPlatform(nvl(request.getPlatform()))
                        .setUrl(nvl(request.getUrl()))
                        .build());
        return ResponseEntity.ok(ApiResponse.<MerchantSocialMediaLinkResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.socialLink(resp.getData()))
                .build());
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantSocialMediaLinkResponse>> update(
            @PathVariable Integer id,
            @RequestBody UpdateMerchantSocialRequest request) {
        request.setId(id);
        var resp = commandStub.update(
                com.sanedge.ecommerce.pb.merchant_social_link.UpdateMerchantSocialRequest.newBuilder()
                        .setId(nvl(request.getId()))
                        .setMerchantDetailId(nvl(request.getMerchantDetailId()))
                        .setPlatform(nvl(request.getPlatform()))
                        .setUrl(nvl(request.getUrl()))
                        .build());
        return ResponseEntity.ok(ApiResponse.<MerchantSocialMediaLinkResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.socialLink(resp.getData()))
                .build());
    }

    // The merchant_social_link proto only exposes Create/Update; the remaining
    // monolith endpoints have no downstream gRPC counterpart yet.

    @PostMapping("/trash/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantSocialMediaLinkResponseDeleteAt>> trash(@PathVariable Integer id) {
        throw new UnsupportedOperationException("merchant-social-link trash is not available over gRPC");
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantSocialMediaLinkResponseDeleteAt>> restore(@PathVariable Integer id) {
        throw new UnsupportedOperationException("merchant-social-link restore is not available over gRPC");
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> delete(@PathVariable Integer id) {
        throw new UnsupportedOperationException("merchant-social-link delete is not available over gRPC");
    }

    @PostMapping("/restore/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> restoreAll() {
        throw new UnsupportedOperationException("merchant-social-link restore-all is not available over gRPC");
    }

    @PostMapping("/delete/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAll() {
        throw new UnsupportedOperationException("merchant-social-link delete-all is not available over gRPC");
    }

    private static int nvl(Integer value) {
        return value == null ? 0 : value;
    }

    private static String nvl(String value) {
        return value == null ? "" : value;
    }
}
