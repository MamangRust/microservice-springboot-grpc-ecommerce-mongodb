package com.sanedge.ecommerce.merchant.models;

import org.springframework.data.mongodb.core.mapping.Document;

import com.sanedge.ecommerce.common.outbox.OutboxEvent;

/**
 * Outbox row for the merchant service. Drained by {@code MerchantOutboxPublisher}
 * and published to the email-service notification topics.
 */
@Document(collection = "merchant_outbox")
public class MerchantOutbox extends OutboxEvent {
}
