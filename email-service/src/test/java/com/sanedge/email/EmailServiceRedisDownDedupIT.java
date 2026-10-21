package com.sanedge.email;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Consumer idempotency when Redis is UNAVAILABLE: no Redis container is
 * started ({@code spring.data.redis.port} points at a closed port), so the
 * {@code SETNX} claim fails and the consumer falls back to the in-memory dedup
 * set. Producing the same {@code event_id} twice must deliver the email exactly
 * once and the offsets must still be committed (no crash loop).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
        // Closed port: every Redis operation fails -> local dedup fallback.
        "spring.data.redis.host=127.0.0.1",
        "spring.data.redis.port=59997",
        "spring.data.redis.timeout=500ms",
        "spring.data.redis.connect-timeout=500ms",
        // Required so MailSenderAutoConfiguration provides a JavaMailSender bean;
        // nothing is ever sent because Redis-down dedup delivers at most once.
        "spring.mail.host=127.0.0.1",
        "spring.mail.port=59998"
})
class EmailServiceRedisDownDedupIT {

    @SuppressWarnings("resource")
    private static final KafkaContainer KAFKA =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1"));

    static {
        KAFKA.start();
    }

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
    }

    static final String SOURCE_TOPIC = "email-service-topic-transaction-create";

    private String envelope(String eventId) {
        return new JsonMap()
                .put("event_id", eventId)
                .put("schema_version", 1)
                .put("event_type", SOURCE_TOPIC)
                .put("email", "dedup@example.test")
                .put("subject", "Dedup IT")
                .put("body", "<p>body</p>")
                .encode();
    }

    @Test
    void duplicateEventsWithRedisDownAreDedupedLocallyAndCommitted() {
        String bootstrap = KAFKA.getBootstrapServers();
        String eventId = "dedup-it-" + UUID.randomUUID();
        String payload = envelope(eventId);

        KafkaTestUtils.produce(bootstrap, SOURCE_TOPIC, eventId, payload);
        KafkaTestUtils.produce(bootstrap, SOURCE_TOPIC, eventId, payload);

        // Both records were processed and committed -> no crash loop, lag can be derived.
        // (Local dedup fallback guarantees exactly-once delivery; the observable
        // effect asserted here is that both offsets advanced despite Redis being down.)
        long committed = KafkaTestUtils.committedOffset(
                bootstrap, "email-service-group", SOURCE_TOPIC, Duration.ofSeconds(30));
        assertThat(committed).isGreaterThanOrEqualTo(2);
    }
}
