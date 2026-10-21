package com.sanedge.ecommerce.common.outbox;

import java.util.Map;

/**
 * Applies {@code KAFKA_*} environment variables to a producer/consumer config map.
 *
 * <p>Mirrors the security logic in the stats-writer module so a single set of env
 * vars secures every Kafka client. Plaintext (no env vars) is the local-dev default.
 */
public final class KafkaSecurity {

    private KafkaSecurity() {
    }

    public static void apply(Map<String, Object> config) {
        apply(config, System.getenv());
    }

    public static void apply(Map<String, Object> config, Map<String, String> env) {
        String protocol = env.getOrDefault("KAFKA_SECURITY_PROTOCOL", "PLAINTEXT");
        if (protocol == null || protocol.isBlank() || "PLAINTEXT".equalsIgnoreCase(protocol)) {
            return;
        }
        config.put("security.protocol", protocol);
        if (protocol.toUpperCase().contains("SASL")) {
            config.put("sasl.mechanism", env.getOrDefault("KAFKA_SASL_MECHANISM", "SCRAM-SHA-256"));
            String jaas = env.getOrDefault("KAFKA_SASL_JAAS_CONFIG", "");
            if (!jaas.isBlank()) {
                config.put("sasl.jaas.config", jaas);
            }
        }
        if (protocol.toUpperCase().contains("SSL")) {
            putIfNotBlank(config, "ssl.truststore.location", env.getOrDefault("KAFKA_SSL_TRUSTSTORE_LOCATION", ""));
            putIfNotBlank(config, "ssl.truststore.password", env.getOrDefault("KAFKA_SSL_TRUSTSTORE_PASSWORD", ""));
            putIfNotBlank(config, "ssl.truststore.type", env.getOrDefault("KAFKA_SSL_TRUSTSTORE_TYPE", "JKS"));
            putIfNotBlank(config, "ssl.keystore.location", env.getOrDefault("KAFKA_SSL_KEYSTORE_LOCATION", ""));
            putIfNotBlank(config, "ssl.keystore.password", env.getOrDefault("KAFKA_SSL_KEYSTORE_PASSWORD", ""));
            putIfNotBlank(config, "ssl.key.password", env.getOrDefault("KAFKA_SSL_KEY_PASSWORD", ""));
        }
    }

    private static void putIfNotBlank(Map<String, Object> config, String key, String value) {
        if (value != null && !value.isBlank()) {
            config.put(key, value);
        }
    }
}
