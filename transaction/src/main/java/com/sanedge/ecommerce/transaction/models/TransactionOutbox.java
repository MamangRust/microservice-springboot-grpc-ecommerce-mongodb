package com.sanedge.ecommerce.transaction.models;

import org.springframework.data.mongodb.core.mapping.Document;

import com.sanedge.ecommerce.common.outbox.OutboxEvent;

/**
 * Outbox row for the transaction service. Drained by
 * {@code TransactionOutboxPublisher} and published to {@code stats.transaction.event}.
 */
@Document(collection = "transaction_outbox")
public class TransactionOutbox extends OutboxEvent {
}
