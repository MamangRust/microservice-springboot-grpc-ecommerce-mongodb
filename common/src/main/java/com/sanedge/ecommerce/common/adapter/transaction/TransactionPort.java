package com.sanedge.ecommerce.common.adapter.transaction;

import com.sanedge.ecommerce.common.adapter.model.Transaction;
import com.sanedge.ecommerce.common.adapter.support.Paged;

/**
 * Port transaction: baca + tulis (purge) + enumerasi. Setara
 * {@code transaction.QueryRepository}/{@code CommandRepository}/{@code BulkRepository}
 * di Go.
 */
public interface TransactionPort {

    Transaction findById(int transactionId);

    /** Cari transaksi berdasarkan order (dipakai untuk memastikan order belum dibayar). */
    Transaction findByOrderId(int orderId);

    Boolean deleteByOrderIdPermanent(int orderId);

    Boolean deleteAll();

    Paged<Transaction> findAll(int page, int pageSize);
}
