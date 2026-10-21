package com.sanedge.ecommerce.common.outbox;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Wires the outbox relay infrastructure shared by the stats event sources.
 *
 * <p>Provides the {@link KafkaTemplate} every {@link KafkaOutboxPublisher} needs
 * and enables Spring's {@code @Scheduled} so the relays actually drain their
 * outbox collections. The template targets {@code spring.kafka.bootstrap-servers}
 * (default {@code localhost:9092}); the per-service deployments override it with
 * the in-cluster broker address and any {@code KAFKA_*} security env vars.
 */
@Configuration
@EnableScheduling
public class OutboxAutoConfiguration {

    @Bean
    public KafkaTemplate<String, String> outboxKafkaTemplate(
            @Value("${spring.kafka.bootstrap-servers:localhost:9092}") String bootstrapServers) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        KafkaSecurity.apply(props);
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
    }
}
