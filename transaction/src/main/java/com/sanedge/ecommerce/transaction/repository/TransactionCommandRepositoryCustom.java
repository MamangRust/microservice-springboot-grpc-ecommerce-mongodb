package com.sanedge.ecommerce.transaction.repository;

import com.sanedge.ecommerce.transaction.models.Transaction;

public interface TransactionCommandRepositoryCustom {
    Transaction trashed(String transactionId);

    Transaction restore(String transactionId);

    Transaction deletePermanent(String transactionId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();

    boolean deleteByOrderPermanent(Integer orderId);
}
