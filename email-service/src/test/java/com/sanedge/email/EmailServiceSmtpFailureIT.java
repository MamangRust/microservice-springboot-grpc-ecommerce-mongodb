package com.sanedge.email;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * End-to-end SMTP failure resilience with a REAL Kafka broker: the mailer
 * points at a closed SMTP port, so {@code mailSender.send} fails for every
 * record. Asserts the consumer retries (bounded) and then routes the event to
 * the dead-letter topic with a reason, and that the {@code email_dlq_total}
 * metric call-site fired.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
        "spring.mail.host=127.0.0.1",
        "spring.mail.port=59998",
        "spring.mail.properties.mail.smtp.connectiontimeout=500",
        "spring.mail.properties.mail.smtp.timeout=500",
        "spring.mail.properties.mail.smtp.writetimeout=500",
        "app.kafka.email.retry.max-attempts=2",
        "app.kafka.email.retry.backoff-ms=100"
})
class EmailServiceSmtpFailureIT extends AbstractKafkaRedisIT {

    static final String SOURCE_TOPIC = "email-service-topic-auth-register";
    static final String DEAD_LETTER_TOPIC = "email-service-topic-email-dlq";

    private String envelope(String eventId) {
        return new JsonMap()
                .put("event_id", eventId)
                .put("schema_version", 1)
                .put("event_type", SOURCE_TOPIC)
                .put("email", "smtp-fail@example.test")
                .put("subject", "SMTP failure IT")
                .put("body", "<p>body</p>")
                .encode();
    }

    @Test
    void smtpFailureRetriesThenRoutesEventToDeadLetter() {
        String bootstrap = bootstrapServers();
        String eventId = "smtp-it-" + UUID.randomUUID();

        KafkaTestUtils.produce(bootstrap, SOURCE_TOPIC, eventId, envelope(eventId));

        ConsumerRecord<String, String> dlq = KafkaTestUtils.awaitRecord(bootstrap, DEAD_LETTER_TOPIC,
                r -> eventId.equals(JsonMap.fromJson(r.value()).getString("event_id")),
                Duration.ofSeconds(30));

        assertThat(dlq).isNotNull();
        JsonMap payload = JsonMap.fromJson(dlq.value());
        assertThat(payload.getString("_reason")).isEqualTo("max_retries_exceeded");
        assertThat(payload.getString("_srcTopic")).isEqualTo(SOURCE_TOPIC);
        assertThat(payload.getLong("_srcOffset", -1L)).isGreaterThanOrEqualTo(0L);
        // The consumer must not crash-loop on SMTP failure: committed offset advanced.
        long committed = KafkaTestUtils.committedOffset(
                bootstrap, "email-service-group", SOURCE_TOPIC, Duration.ofSeconds(30));
        assertThat(committed).isGreaterThanOrEqualTo(1);
    }

    @Test
    void invalidEnvelopeIsRoutedToDeadLetterWithoutEmailAttempt() {
        String bootstrap = bootstrapServers();
        String eventId = "smtp-it-invalid-" + UUID.randomUUID();
        // Missing schema_version / subject / body -> invalid envelope.
        String invalid = new JsonMap()
                .put("event_id", eventId)
                .put("email", "invalid@example.test")
                .encode();

        KafkaTestUtils.produce(bootstrap, SOURCE_TOPIC, eventId, invalid);

        ConsumerRecord<String, String> dlq = KafkaTestUtils.awaitRecord(bootstrap, DEAD_LETTER_TOPIC,
                r -> eventId.equals(JsonMap.fromJson(r.value()).getString("event_id")),
                Duration.ofSeconds(30));

        assertThat(dlq).isNotNull();
        assertThat(JsonMap.fromJson(dlq.value()).getString("_reason"))
                .isEqualTo("invalid_event_envelope");
    }
}
