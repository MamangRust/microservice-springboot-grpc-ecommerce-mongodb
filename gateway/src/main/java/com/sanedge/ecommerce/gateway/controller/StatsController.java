package com.sanedge.ecommerce.gateway.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.gateway.dto.responses.stats.StatItemResponse;
import com.sanedge.ecommerce.pb.stats.StatsReaderServiceGrpc;
import com.sanedge.ecommerce.pb.stats.StatsRequest;
import com.sanedge.ecommerce.pb.stats.StatsResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsReaderServiceGrpc.StatsReaderServiceBlockingStub statsReaderStub;

    @GetMapping("/monthly-revenue")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<StatItemResponse>>> monthlyRevenue(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        StatsResponse resp = statsReaderStub.findMonthlyTotalRevenue(
                StatsRequest.newBuilder().setYear(nvl(year)).setMonth(nvl(month)).build());
        return ResponseEntity.ok(toApiResponse(resp));
    }

    @GetMapping("/yearly-revenue")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<StatItemResponse>>> yearlyRevenue(
            @RequestParam Integer year) {
        StatsResponse resp = statsReaderStub.findYearlyTotalRevenue(
                StatsRequest.newBuilder().setYear(nvl(year)).build());
        return ResponseEntity.ok(toApiResponse(resp));
    }

    @GetMapping("/merchant-sales")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<StatItemResponse>>> merchantSales(
            @RequestParam Long merchantId,
            @RequestParam Integer year) {
        StatsResponse resp = statsReaderStub.findMerchantSales(
                StatsRequest.newBuilder().setMerchantId(nvl(merchantId)).setYear(nvl(year)).build());
        return ResponseEntity.ok(toApiResponse(resp));
    }

    @GetMapping("/product-sales")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<StatItemResponse>>> productSales(
            @RequestParam Long productId,
            @RequestParam Integer year) {
        StatsResponse resp = statsReaderStub.findProductSales(
                StatsRequest.newBuilder().setProductId(nvl(productId)).setYear(nvl(year)).build());
        return ResponseEntity.ok(toApiResponse(resp));
    }

    private ApiResponse<List<StatItemResponse>> toApiResponse(StatsResponse resp) {
        List<StatItemResponse> data = resp.getDataList().stream()
                .map(item -> StatItemResponse.builder()
                        .period(item.getPeriod())
                        .dimensionId(item.getDimensionId())
                        .value(item.getValue())
                        .build())
                .toList();
        return ApiResponse.<List<StatItemResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(data)
                .build();
    }

    private static int nvl(Integer value) {
        return value == null ? 0 : value;
    }

    private static long nvl(Long value) {
        return value == null ? 0L : value;
    }
}
