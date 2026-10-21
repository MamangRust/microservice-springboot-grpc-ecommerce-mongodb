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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.protobuf.Empty;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.gateway.dto.requests.transactions.CreateTransactionRequest;
import com.sanedge.ecommerce.gateway.dto.requests.transactions.FindAllTransactionByMerchantRequest;
import com.sanedge.ecommerce.gateway.dto.requests.transactions.FindAllTransactionRequest;
import com.sanedge.ecommerce.gateway.dto.requests.transactions.MonthAmountTransactionMerchant;
import com.sanedge.ecommerce.gateway.dto.requests.transactions.MonthAmountTransactionRequest;
import com.sanedge.ecommerce.gateway.dto.requests.transactions.MonthMethodTransactionMerchantRequest;
import com.sanedge.ecommerce.gateway.dto.requests.transactions.MonthMethodTransactionRequest;
import com.sanedge.ecommerce.gateway.dto.requests.transactions.UpdateTransactionRequest;
import com.sanedge.ecommerce.gateway.dto.requests.transactions.YearAmountTransactionMerchant;
import com.sanedge.ecommerce.gateway.dto.requests.transactions.YearMethodTransactionMerchantRequest;
import com.sanedge.ecommerce.gateway.dto.responses.transaction.TransactionMonthlyAmountFailedResponse;
import com.sanedge.ecommerce.gateway.dto.responses.transaction.TransactionMonthlyAmountSuccessResponse;
import com.sanedge.ecommerce.gateway.dto.responses.transaction.TransactionMonthlyMethodResponse;
import com.sanedge.ecommerce.gateway.dto.responses.transaction.TransactionResponse;
import com.sanedge.ecommerce.gateway.dto.responses.transaction.TransactionResponseDeleteAt;
import com.sanedge.ecommerce.gateway.dto.responses.transaction.TransactionYearlyAmountFailedResponse;
import com.sanedge.ecommerce.gateway.dto.responses.transaction.TransactionYearlyAmountSuccessResponse;
import com.sanedge.ecommerce.gateway.dto.responses.transaction.TransactionYearlyMethodResponse;
import com.sanedge.ecommerce.gateway.mapper.ProtoMapper;
import com.sanedge.ecommerce.pb.transaction.TransactionCommandServiceGrpc;
import com.sanedge.ecommerce.pb.transaction.TransactionQueryServiceGrpc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transaction")
public class TransactionController {

    private final TransactionQueryServiceGrpc.TransactionQueryServiceBlockingStub transactionQueryStub;
    private final TransactionCommandServiceGrpc.TransactionCommandServiceBlockingStub transactionCommandStub;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponsePagination<List<TransactionResponse>>> findAllTransactions(
            @ModelAttribute FindAllTransactionRequest req) {
        var resp = transactionQueryStub.findAllTransactions(
                com.sanedge.ecommerce.pb.transaction.FindAllTransactionRequest.newBuilder()
                        .setPage(nvl(req.getPage()))
                        .setPageSize(nvl(req.getPageSize()))
                        .setSearch(nvl(req.getSearch()))
                        .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<TransactionResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactions(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<TransactionResponseDeleteAt>>> findActiveTransactions(
            @ModelAttribute FindAllTransactionRequest req) {
        var resp = transactionQueryStub.findByActive(
                com.sanedge.ecommerce.pb.transaction.FindAllTransactionRequest.newBuilder()
                        .setPage(nvl(req.getPage()))
                        .setPageSize(nvl(req.getPageSize()))
                        .setSearch(nvl(req.getSearch()))
                        .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<TransactionResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactionsDeleteAtFromResponse(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<TransactionResponseDeleteAt>>> findTrashedTransactions(
            @ModelAttribute FindAllTransactionRequest req) {
        var resp = transactionQueryStub.findByTrashed(
                com.sanedge.ecommerce.pb.transaction.FindAllTransactionRequest.newBuilder()
                        .setPage(nvl(req.getPage()))
                        .setPageSize(nvl(req.getPageSize()))
                        .setSearch(nvl(req.getSearch()))
                        .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<TransactionResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactionsDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<TransactionResponse>> findTransactionById(@PathVariable Integer id) {
        var resp = transactionQueryStub.findById(
                com.sanedge.ecommerce.pb.transaction.FindByIdTransactionRequest.newBuilder().setId(id).build());
        return ResponseEntity.ok(ApiResponse.<TransactionResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transaction(resp.getData()))
                .build());
    }

    @GetMapping("/merchant/{merchantId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<TransactionResponse>>> findByMerchant(
            @PathVariable Integer merchantId,
            @ModelAttribute FindAllTransactionByMerchantRequest req) {
        req.setMerchantId(merchantId);
        var resp = transactionQueryStub.findByMerchant(
                com.sanedge.ecommerce.pb.transaction.FindAllTransactionByMerchantRequest.newBuilder()
                        .setMerchantId(nvl(req.getMerchantId()))
                        .setPage(nvl(req.getPage()))
                        .setPageSize(nvl(req.getPageSize()))
                        .setSearch(nvl(req.getSearch()))
                        .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<TransactionResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactions(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPagination()))
                .build());
    }


    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<TransactionResponse>> createTransaction(
            @Valid @RequestBody CreateTransactionRequest request) {
        var resp = transactionCommandStub.create(
                com.sanedge.ecommerce.pb.transaction.CreateTransactionRequest.newBuilder()
                        .setOrderId(nvl(request.getOrderID()))
                        .setMerchantId(nvl(request.getMerchantID()))
                        .setPaymentMethod(nvl(request.getPaymentMethod()))
                        .setAmount(nvl(request.getAmount()))
                        .setPaymentStatus(nvl(request.getPaymentStatus()))
                        .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<TransactionResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transaction(resp.getData()))
                .build());
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<TransactionResponse>> updateTransaction(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateTransactionRequest request) {
        request.setTransactionID(id);
        var resp = transactionCommandStub.update(
                com.sanedge.ecommerce.pb.transaction.UpdateTransactionRequest.newBuilder()
                        .setTransactionId(nvl(request.getTransactionID()))
                        .setOrderId(nvl(request.getOrderID()))
                        .setMerchantId(nvl(request.getMerchantID()))
                        .setPaymentMethod(nvl(request.getPaymentMethod()))
                        .setAmount(nvl(request.getAmount()))
                        .setPaymentStatus(nvl(request.getPaymentStatus()))
                        .build());
        return ResponseEntity.ok(ApiResponse.<TransactionResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transaction(resp.getData()))
                .build());
    }

    @PostMapping("/trashed/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<TransactionResponseDeleteAt>> trashTransaction(@PathVariable Integer id) {
        var resp = transactionCommandStub.trashedTransaction(
                com.sanedge.ecommerce.pb.transaction.FindByIdTransactionRequest.newBuilder().setId(id).build());
        return ResponseEntity.ok(ApiResponse.<TransactionResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactionDeleteAt(resp.getData()))
                .build());
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<TransactionResponseDeleteAt>> restoreTransaction(@PathVariable Integer id) {
        var resp = transactionCommandStub.restoreTransaction(
                com.sanedge.ecommerce.pb.transaction.FindByIdTransactionRequest.newBuilder().setId(id).build());
        return ResponseEntity.ok(ApiResponse.<TransactionResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactionDeleteAt(resp.getData()))
                .build());
    }

    @DeleteMapping("/permanent/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteTransaction(@PathVariable Integer id) {
        var resp = transactionCommandStub.deleteTransactionPermanent(
                com.sanedge.ecommerce.pb.transaction.FindByIdTransactionRequest.newBuilder().setId(id).build());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/restore/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> restoreAllTrashedTransactions() {
        var resp = transactionCommandStub.restoreAllTransaction(Empty.getDefaultInstance());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/permanent/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllTrashedTransactions() {
        var resp = transactionCommandStub.deleteAllTransactionPermanent(Empty.getDefaultInstance());
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
