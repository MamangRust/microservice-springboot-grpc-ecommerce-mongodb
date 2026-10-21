package com.sanedge.ecommerce.transaction.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.protobuf.StringValue;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.common.grpc.GrpcErrorMapper;
import com.sanedge.ecommerce.common.grpc.ProtoMappers;
import com.sanedge.ecommerce.pb.transaction.ApiResponsePaginationTransaction;
import com.sanedge.ecommerce.pb.transaction.ApiResponsePaginationTransactionDeleteAt;
import com.sanedge.ecommerce.pb.transaction.ApiResponseTransaction;
import com.sanedge.ecommerce.pb.transaction.FindAllTransactionByMerchantRequest;
import com.sanedge.ecommerce.pb.transaction.FindAllTransactionRequest;
import com.sanedge.ecommerce.pb.transaction.FindByIdTransactionRequest;
import com.sanedge.ecommerce.pb.transaction.FindByOrderIdTransactionRequest;
import com.sanedge.ecommerce.pb.transaction.TransactionQueryServiceGrpc;
import com.sanedge.ecommerce.pb.transaction.TransactionResponse;
import com.sanedge.ecommerce.pb.transaction.TransactionResponseDeleteAt;
import com.sanedge.ecommerce.transaction.service.TransactionQueryService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionQueryHandler extends TransactionQueryServiceGrpc.TransactionQueryServiceImplBase {

    private final TransactionQueryService transactionQueryService;

    @Override
    public void findAllTransactions(FindAllTransactionRequest request,
            StreamObserver<ApiResponsePaginationTransaction> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.transaction.domain.responses.TransactionResponse>> res = transactionQueryService
                    .findAllTransactions(toFindAll(request));

            ApiResponsePaginationTransaction.Builder builder = ApiResponsePaginationTransaction.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setPagination(ProtoMappers.toProtoPagination(res.getPagination()));
            if (res.getData() != null) {
                res.getData().forEach(r -> builder.addData(toProto(r)));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findByMerchant(FindAllTransactionByMerchantRequest request,
            StreamObserver<ApiResponsePaginationTransaction> observer) {
        try {
            com.sanedge.ecommerce.transaction.domain.requests.FindAllTransactionByMerchantRequest req = new com.sanedge.ecommerce.transaction.domain.requests.FindAllTransactionByMerchantRequest();
            req.setMerchantId(request.getMerchantId());
            req.setSearch(request.getSearch());
            req.setPage(request.getPage());
            req.setPageSize(request.getPageSize());

            ApiResponsePagination<List<com.sanedge.ecommerce.transaction.domain.responses.TransactionResponse>> res = transactionQueryService
                    .findByMerchant(req);

            ApiResponsePaginationTransaction.Builder builder = ApiResponsePaginationTransaction.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setPagination(ProtoMappers.toProtoPagination(res.getPagination()));
            if (res.getData() != null) {
                res.getData().forEach(r -> builder.addData(toProto(r)));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findById(FindByIdTransactionRequest request, StreamObserver<ApiResponseTransaction> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.transaction.domain.responses.TransactionResponse> res = transactionQueryService
                    .findById(request.getId());

            observer.onNext(ApiResponseTransaction.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setData(toProto(res.getData()))
                    .build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findByOrderId(FindByOrderIdTransactionRequest request,
            StreamObserver<ApiResponseTransaction> observer) {
        try {
            ApiResponse<com.sanedge.ecommerce.transaction.domain.responses.TransactionResponse> res = transactionQueryService
                    .findByOrderId(request.getOrderId());

            observer.onNext(ApiResponseTransaction.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setData(toProto(res.getData()))
                    .build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findByActive(FindAllTransactionRequest request,
            StreamObserver<ApiResponsePaginationTransaction> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.transaction.domain.responses.TransactionResponseDeleteAt>> res = transactionQueryService
                    .findByActive(toFindAll(request));

            ApiResponsePaginationTransaction.Builder builder = ApiResponsePaginationTransaction.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setPagination(ProtoMappers.toProtoPagination(res.getPagination()));
            if (res.getData() != null) {
                res.getData().forEach(r -> builder.addData(toProtoFromDeleteAt(r)));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findByTrashed(FindAllTransactionRequest request,
            StreamObserver<ApiResponsePaginationTransactionDeleteAt> observer) {
        try {
            ApiResponsePagination<List<com.sanedge.ecommerce.transaction.domain.responses.TransactionResponseDeleteAt>> res = transactionQueryService
                    .findByTrashed(toFindAll(request));

            ApiResponsePaginationTransactionDeleteAt.Builder builder = ApiResponsePaginationTransactionDeleteAt
                    .newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()))
                    .setPagination(ProtoMappers.toProtoPagination(res.getPagination()));
            if (res.getData() != null) {
                res.getData().forEach(r -> builder.addData(toProtoDeleteAt(r)));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private com.sanedge.ecommerce.transaction.domain.requests.FindAllTransactionRequest toFindAll(
            FindAllTransactionRequest request) {
        com.sanedge.ecommerce.transaction.domain.requests.FindAllTransactionRequest req = new com.sanedge.ecommerce.transaction.domain.requests.FindAllTransactionRequest();
        req.setPage(request.getPage());
        req.setPageSize(request.getPageSize());
        req.setSearch(request.getSearch());
        return req;
    }

    private TransactionResponse toProto(com.sanedge.ecommerce.transaction.domain.responses.TransactionResponse r) {
        if (r == null) {
            return TransactionResponse.getDefaultInstance();
        }
        return TransactionResponse.newBuilder()
                .setId(r.getId() == null ? 0 : r.getId().intValue())
                .setOrderId(r.getOrderId() == null ? 0 : r.getOrderId())
                .setMerchantId(r.getMerchantId() == null ? 0 : r.getMerchantId())
                .setPaymentMethod(ProtoMappers.nvl(r.getPaymentMethod()))
                .setAmount(r.getAmount() == null ? 0 : r.getAmount())
                .setPaymentStatus(ProtoMappers.nvl(r.getPaymentStatus()))
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .build();
    }

    private TransactionResponse toProtoFromDeleteAt(
            com.sanedge.ecommerce.transaction.domain.responses.TransactionResponseDeleteAt r) {
        if (r == null) {
            return TransactionResponse.getDefaultInstance();
        }
        return TransactionResponse.newBuilder()
                .setId(r.getId() == null ? 0 : r.getId().intValue())
                .setOrderId(r.getOrderId() == null ? 0 : r.getOrderId())
                .setMerchantId(r.getMerchantId() == null ? 0 : r.getMerchantId())
                .setPaymentMethod(ProtoMappers.nvl(r.getPaymentMethod()))
                .setAmount(r.getAmount() == null ? 0 : r.getAmount())
                .setPaymentStatus(ProtoMappers.nvl(r.getPaymentStatus()))
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .build();
    }

    private TransactionResponseDeleteAt toProtoDeleteAt(
            com.sanedge.ecommerce.transaction.domain.responses.TransactionResponseDeleteAt r) {
        if (r == null) {
            return TransactionResponseDeleteAt.getDefaultInstance();
        }
        return TransactionResponseDeleteAt.newBuilder()
                .setId(r.getId() == null ? 0 : r.getId().intValue())
                .setOrderId(r.getOrderId() == null ? 0 : r.getOrderId())
                .setMerchantId(r.getMerchantId() == null ? 0 : r.getMerchantId())
                .setPaymentMethod(ProtoMappers.nvl(r.getPaymentMethod()))
                .setAmount(r.getAmount() == null ? 0 : r.getAmount())
                .setPaymentStatus(ProtoMappers.nvl(r.getPaymentStatus()))
                .setCreatedAt(ProtoMappers.nvl(r.getCreatedAt()))
                .setUpdatedAt(ProtoMappers.nvl(r.getUpdatedAt()))
                .setDeletedAt(StringValue.of(ProtoMappers.nvl(r.getDeletedAt())))
                .build();
    }
}
