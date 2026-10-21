package com.sanedge.email;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Kafka-driven email worker (Spring Boot port of the Quarkus reactive worker).
 *
 * <p>Semantics preserved from the original:
 * <ul>
 *   <li>Manual offset commits; a record is only committed after it is fully
 *       processed (or intentionally skipped as duplicate).</li>
 *   <li>Redis idempotency: a short {@code PROCESSING} lease flips to a terminal
 *       {@code SENT} marker (full dedup TTL). Fail-open when Redis is down.</li>
 *   <li>Failed sends go to the retry topic with backoff metadata; exhausted
 *       attempts go to the DLQ.</li>
 * </ul>
 */
@Component
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    public static final String RETRY_TOPIC = "email-service-topic-email-retry";
    public static final String DEAD_LETTER_TOPIC = "email-service-topic-email-dlq";

    private static final int EVENT_SCHEMA_VERSION = 1;
    private static final List<String> INTERNAL_FIELDS = List.of(
            "_srcTopic", "_srcPartition", "_srcOffset", "_attempt", "_retryAt", "_reason");
    private static final String STATE_PROCESSING = "PROCESSING";
    private static final String STATE_SENT = "SENT";

    public static final List<String> TOPICS = List.of(
            "email-service-topic-auth-register",
            "email-service-topic-auth-forgot-password",
            "email-service-topic-auth-verify-code-success",
            "email-service-topic-merchant-create",
            "email-service-topic-merchant-update-status",
            "email-service-topic-merchant-document-create",
            "email-service-topic-merchant-document-update-status",
            "email-service-topic-transaction-create");

    /** Result of an atomic idempotency claim. */
    enum ClaimResult {
        /** This caller owns the lease and must send the email. */
        CLAIMED,
        /** Event already reached a terminal SENT state — skip. */
        DUPLICATE,
        /** Another processor holds an active lease — do not send, retry later. */
        BUSY
    }

    private final JavaMailSender mailSender;
    private final StringRedisTemplate redis;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final EmailMetrics metrics;
    private final EmailProperties properties;
    private final ObjectMapper objectMapper;
    private final String mailFrom;

    private final Set<String> processedEventIds = ConcurrentHashMap.newKeySet();
    private final Map<String, Long> partitionLag = new ConcurrentHashMap<>();

    public EmailService(JavaMailSender mailSender,
            StringRedisTemplate redis,
            KafkaTemplate<String, String> kafkaTemplate,
            EmailMetrics metrics,
            EmailProperties properties,
            ObjectMapper objectMapper,
            @Value("${app.mail.from:no-reply@payment-gateway.com}") String mailFrom) {
        this.mailSender = mailSender;
        this.redis = redis;
        this.kafkaTemplate = kafkaTemplate;
        this.metrics = metrics;
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.mailFrom = mailFrom;
    }

    @KafkaListener(topics = {
            "email-service-topic-auth-register",
            "email-service-topic-auth-forgot-password",
            "email-service-topic-auth-verify-code-success",
            "email-service-topic-merchant-create",
            "email-service-topic-merchant-update-status",
            "email-service-topic-merchant-document-create",
            "email-service-topic-merchant-document-update-status",
            "email-service-topic-transaction-create"
    }, groupId = "${app.kafka.email.group-id:email-service-group}",
            containerFactory = "manualAckListenerContainerFactory")
    public void onMessage(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            JsonMap payload = parse(record.value());
            processRecord(record.topic(), record.partition(), record.offset(), record.key(), payload, 0);
            recordCommittedPosition(record.topic(), record.partition(), record.offset() + 1);
            ack.acknowledge();
        } catch (Exception error) {
            // Never commit a failed record: Kafka commits are monotonic per
            // partition, so committing would acknowledge the failure too.
            log.error("Email record was not committed; will be redelivered topic={} partition={} offset={}",
                    record.topic(), record.partition(), record.offset(), error);
        }
    }

    /**
     * Process one logical notification. Package-private so {@link RetryProcessor}
     * can re-enter with the original source coordinates and the attempt counter.
     */
    void processRecord(String srcTopic, int srcPartition, long srcOffset, String key,
            JsonMap payload, int attempt) {
        long startNanos = System.nanoTime();
        try {
            processRecordInternal(srcTopic, srcPartition, srcOffset, key, payload, attempt);
        } finally {
            metrics.recordProcessingDuration((System.nanoTime() - startNanos) / 1_000_000_000.0);
        }
    }

    private void processRecordInternal(String srcTopic, int srcPartition, long srcOffset, String key,
            JsonMap payload, int attempt) {
        if (!hasValidEnvelope(payload)) {
            metrics.recordInvalid();
            sendDeadLetter(payload, key, srcTopic, srcPartition, srcOffset, "invalid_event_envelope");
            return;
        }

        String eventId = payload.getString("event_id");
        switch (claimEvent(eventId)) {
            case DUPLICATE:
                log.info("Skipping duplicate email event_id={}", eventId);
                metrics.recordDuplicate();
                return;
            case BUSY:
                throw new IllegalStateException("Email idempotency claim busy, retrying event_id=" + eventId);
            default: // CLAIMED
                try {
                    sendEmail(payload);
                    markSent(eventId);
                } catch (RuntimeException error) {
                    releaseEvent(eventId);
                    routeToRetryOrDlq(payload, key, srcTopic, srcPartition, srcOffset, attempt, error);
                }
        }
    }

    private void routeToRetryOrDlq(JsonMap payload, String key, String srcTopic,
            int srcPartition, long srcOffset, int attempt, Throwable error) {
        metrics.recordFailed();
        int nextAttempt = attempt + 1;
        if (nextAttempt >= properties.getRetryMaxAttempts()) {
            sendDeadLetter(payload, key, srcTopic, srcPartition, srcOffset, "max_retries_exceeded");
            return;
        }
        metrics.recordRetried();

        JsonMap retry = payload.copy()
                .put("_srcTopic", srcTopic)
                .put("_srcPartition", srcPartition)
                .put("_srcOffset", srcOffset)
                .put("_attempt", nextAttempt)
                .put("_retryAt", System.currentTimeMillis() + backoffMs(nextAttempt))
                .put("_reason", error.getMessage());

        log.warn("Email send failed; scheduled retry {} of {} | event_id={} reason={}",
                nextAttempt, properties.getRetryMaxAttempts(), payload.getString("event_id"), error.getMessage());
        sendToTopic(RETRY_TOPIC, key, retry);
    }

    /**
     * Atomically claims {@code eventId} for processing. The claim is a short
     * lease (PROCESSING); if the process crashes before sending, the key expires
     * and the event is re-claimed. Fail-open: on Redis outage the in-memory set
     * is used and the email still goes out (availability &gt; exactly-once).
     */
    private ClaimResult claimEvent(String eventId) {
        String key = idempotencyKey(eventId);
        if (redis == null) {
            return processedEventIds.add(eventId) ? ClaimResult.CLAIMED : ClaimResult.DUPLICATE;
        }
        try {
            Boolean claimed = redis.opsForValue().setIfAbsent(
                    key, STATE_PROCESSING, Duration.ofSeconds(properties.getIdempotency().getLeaseSeconds()));
            if (Boolean.TRUE.equals(claimed)) {
                return ClaimResult.CLAIMED;
            }
            String state = redis.opsForValue().get(key);
            return STATE_SENT.equals(state) ? ClaimResult.DUPLICATE : ClaimResult.BUSY;
        } catch (RuntimeException error) {
            log.warn("Redis idempotency unavailable; using local dedup fallback", error);
            return processedEventIds.add(eventId) ? ClaimResult.CLAIMED : ClaimResult.DUPLICATE;
        }
    }

    /** Flips the event to the terminal SENT state (full dedup TTL). */
    private void markSent(String eventId) {
        if (redis == null) {
            return;
        }
        try {
            redis.opsForValue().set(idempotencyKey(eventId), STATE_SENT,
                    Duration.ofSeconds(properties.getIdempotency().getTtlSeconds()));
        } catch (RuntimeException error) {
            log.debug("Failed to mark email event SENT", error);
        }
    }

    /** Releases the claim so a failed send can be retried. */
    private void releaseEvent(String eventId) {
        processedEventIds.remove(eventId);
        if (redis == null) {
            return;
        }
        try {
            redis.delete(idempotencyKey(eventId));
        } catch (RuntimeException error) {
            log.debug("Failed to release email idempotency claim", error);
        }
    }

    private String idempotencyKey(String eventId) {
        return "email:idempotency:" + eventId;
    }

    private boolean hasValidEnvelope(JsonMap payload) {
        return payload != null
                && payload.getString("event_id") != null
                && payload.getInt("schema_version", 0) == EVENT_SCHEMA_VERSION
                && payload.getString("event_type") != null
                && payload.getString("email") != null
                && payload.getString("subject") != null
                && payload.getString("body") != null;
    }

    private void sendEmail(JsonMap payload) {
        if (payload == null
                || payload.getString("email") == null
                || payload.getString("subject") == null
                || payload.getString("body") == null) {
            // Log only envelope presence, never the raw payload (may contain PII).
            log.warn("Received incomplete email payload (event_id={}, has_email={}, has_subject={}, has_body={})",
                    payload == null ? null : payload.getString("event_id"),
                    payload != null && payload.getString("email") != null,
                    payload != null && payload.getString("subject") != null,
                    payload != null && payload.getString("body") != null);
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(payload.getString("email"));
        message.setSubject(payload.getString("subject"));
        message.setText(payload.getString("body"));
        mailSender.send(message);
        metrics.recordSent();
        log.info("Email sent event_id={} recipient={}", payload.getString("event_id"), payload.getString("email"));
    }

    private void sendDeadLetter(JsonMap payload, String key, String srcTopic,
            int srcPartition, long srcOffset, String reason) {
        JsonMap dlq = payload == null ? new JsonMap() : payload.copy();
        for (String field : INTERNAL_FIELDS) {
            dlq.remove(field);
        }
        dlq.put("_reason", reason)
                .put("_srcTopic", srcTopic)
                .put("_srcPartition", srcPartition)
                .put("_srcOffset", srcOffset);
        kafkaTemplate.send(DEAD_LETTER_TOPIC, key, dlq.encode());
        metrics.recordDlqSent();
        log.warn("Email event routed to DLQ event_id={} offset={} reason={}",
                payload == null ? null : payload.getString("event_id"), srcOffset, reason);
    }

    private void sendToTopic(String topic, String key, JsonMap payload) {
        kafkaTemplate.send(topic, key, payload.encode());
    }

    private long backoffMs(int attempt) {
        return Math.min(attempt * properties.getRetryBackoffMs(), 300_000L);
    }

    /** Periodically refreshes the per-partition lag gauge. */
    @Scheduled(fixedDelayString = "${app.kafka.email.lag.poll-ms:15000}")
    public void refreshLag() {
        metrics.refreshKafkaLag(new HashMap<>(partitionLag));
    }

    void recordCommittedPosition(String topic, int partition, long nextOffset) {
        partitionLag.put(topic + "-" + partition, nextOffset);
    }

    private JsonMap parse(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            Map<String, Object> map = objectMapper.readValue(value, new TypeReference<Map<String, Object>>() {
            });
            return new JsonMap(map);
        } catch (Exception error) {
            log.warn("Failed to parse Kafka record payload: {}", error.getMessage());
            return null;
        }
    }
}
