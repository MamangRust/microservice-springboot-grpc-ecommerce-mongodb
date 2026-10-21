package com.sanedge.ecommerce.order.models;

import org.springframework.data.mongodb.core.mapping.Document;

import com.sanedge.ecommerce.common.outbox.OutboxEvent;

/**
 * Outbox row for the order service. Drained by {@code OrderOutboxPublisher} and
 * published to {@code stats.ecommerce.order.event} / {@code stats.ecommerce.order_item.event}.
 */
@Document(collection = "order_outbox")
public class OrderOutbox extends OutboxEvent {
}
