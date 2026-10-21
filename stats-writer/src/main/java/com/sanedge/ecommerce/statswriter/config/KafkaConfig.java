package com.sanedge.ecommerce.statswriter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

/**
 * Manual-commit Kafka wiring for the stats writer (mirrors the email worker).
 */
@Configuration
public class KafkaConfig {

    private final String bootstrapServers;

    public KafkaConfig(@Value("${spring.kafka.bootstrap-servers:localhost:9092}") String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    @Bean
    public ConsumerFactory<String, String> statsConsumerFactory(
            @Value("${app.kafka.stats.group-id:stats-writer-group}") String groupId) {
        return new DefaultKafkaConsumerFactory<>(KafkaSecurityConfig.consumer(bootstrapServers, groupId));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> statsListenerContainerFactory(
            ConsumerFactory<String, String> statsConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(statsConsumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }
}
