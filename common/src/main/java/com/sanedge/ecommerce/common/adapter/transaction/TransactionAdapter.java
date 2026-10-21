package com.sanedge.ecommerce.common.adapter.transaction;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.Empty;
import com.sanedge.ecommerce.common.adapter.model.Transaction;
import com.sanedge.ecommerce.common.adapter.support.Paged;
import com.sanedge.ecommerce.common.adapter.support.ProtoTime;
import com.sanedge.ecommerce.pb.transaction.FindAllTransactionRequest;
import com.sanedge.ecommerce.pb.transaction.FindByOrderIdTransactionRequest;
import com.sanedge.ecommerce.pb.transaction.FindByIdTransactionRequest;
import com.sanedge.ecommerce.pb.transaction.TransactionCommandServiceGrpc.TransactionCommandServiceBlockingStub;
import com.sanedge.ecommerce.pb.transaction.TransactionQueryServiceGrpc.TransactionQueryServiceBlockingStub;
import com.sanedge.ecommerce.pb.transaction.TransactionResponse;

public class TransactionAdapter implements TransactionPort {

    private final TransactionQueryServiceBlockingStub query;

    private final TransactionCommandServiceBlockingStub command;

    public TransactionAdapter(TransactionQueryServiceBlockingStub query, TransactionCommandServiceBlockingStub command) {
        this.query = query;
        this.command = command;
    }

    @Override
    public Transaction findById(int transactionId) {
        var resp = query.findById(FindByIdTransactionRequest.newBuilder().setId(transactionId).build());
        return toModel(resp.getData());
    }

    @Override
    public Transaction findByOrderId(int orderId) {
        var resp = query.findByOrderId(FindByOrderIdTransactionRequest.newBuilder()
                .setOrderId(orderId)
                .build());
        return toModel(resp.getData());
    }

    @Override
    public Boolean deleteByOrderIdPermanent(int orderId) {
        var resp = command.deleteTransactionByOrderPermanent(FindByIdTransactionRequest.newBuilder().setId(orderId).build());
        return "success".equals(resp.getStatus());
    }

    @Override
    public Boolean deleteAll() {
        var resp = command.deleteAllTransactionPermanent(Empty.getDefaultInstance());
        return "success".equals(resp.getStatus());
    }

    @Override
    public Paged<Transaction> findAll(int page, int pageSize) {
        var resp = query.findAllTransactions(FindAllTransactionRequest.newBuilder()
                .setPage(page)
                .setPageSize(pageSize)
                .build());
        List<Transaction> items = new ArrayList<>(resp.getDataList().size());
        for (TransactionResponse t : resp.getDataList()) {
            items.add(toModel(t));
        }
        int total = resp.hasPagination() ? resp.getPagination().getTotalRecords() : items.size();
        return Paged.of(items, total);
    }

    static Transaction toModel(TransactionResponse t) {
        if (t == null) {
            return null;
        }
        return new Transaction(
                t.getId(),
                t.getOrderId(),
                t.getMerchantId(),
                t.getPaymentMethod(),
                t.getAmount(),
                t.getPaymentStatus(),
                ProtoTime.parse(t.getCreatedAt()),
                ProtoTime.parse(t.getUpdatedAt()));
    }
}
