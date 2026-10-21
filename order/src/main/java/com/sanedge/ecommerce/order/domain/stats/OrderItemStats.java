package com.sanedge.ecommerce.order.domain.stats;

/**
 * Per-order-item stats projection emitted to {@code stats.ecommerce.order_item.event}.
 *
 * <p>Mirrors the Quarkus order service's {@code OrderItemStats}; the payload keys
 * are kept identical so a Quarkus-style consumer can ingest it unchanged.
 */
public record OrderItemStats(
        int orderItemId,
        String orderId,
        int merchantId,
        int categoryId,
        int productId,
        int quantity,
        int unitPrice,
        int subtotal) {
}
