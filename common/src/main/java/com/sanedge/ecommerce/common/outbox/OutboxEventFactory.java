package com.sanedge.ecommerce.common.outbox;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Builds the JSON payloads stored in outbox rows.
 *
 * <p>The {@code stats(...)} helper produces the exact JSON consumed by
 * {@code stats-writer} ({@code period, amount, merchantId, productId, metric}).
 * The {@code metric} value is derived from the topic so the writer can partition
 * the row in ClickHouse.
 */
public final class OutboxEventFactory {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private OutboxEventFactory() {
    }

    /** Immutable (eventId, payload) pair carried by every envelope. */
    public record Envelope(String eventId, String payload) {
    }

    public static Envelope email(String topic, String to, String subject, String body) {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("to", to);
        payload.put("subject", subject);
        payload.put("body", body);
        return new Envelope(UUID.randomUUID().toString(), writeJson(payload));
    }

    public static Envelope stats(String topic, String period, double amount, long merchantId, long productId) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("period", period);
        payload.put("amount", amount);
        payload.put("merchantId", merchantId);
        payload.put("productId", productId);
        payload.put("metric", metricFromTopic(topic));
        return new Envelope(UUID.randomUUID().toString(), writeJson(payload));
    }

    private static String metricFromTopic(String topic) {
        if (topic == null) {
            return "txn";
        }
        if (topic.contains("transaction")) {
            return "txn";
        }
        if (topic.contains("order")) {
            return "order";
        }
        if (topic.contains("merchant")) {
            return "merchant";
        }
        return "txn";
    }

    private static String writeJson(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("failed to serialize outbox payload", e);
        }
    }
}
