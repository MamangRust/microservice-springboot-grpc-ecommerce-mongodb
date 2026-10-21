package com.sanedge.migration.topology;

import java.util.List;

/**
 * Single source of truth for the email topics that the e-commerce modules
 * produce and the {@code email-service} worker consumes.
 *
 * <p>Only {@code spring-kafka} (and its Kafka client) is on this module's
 * classpath, so the producer/consumer classes cannot be imported — their
 * {@code TOPIC_*} constants would drag {@code common} and every producer module
 * into the migration runner. The list below is therefore an explicit mirror of:
 * <ul>
 *   <li>producers — the outbox publishers in {@code auth} (register,
 *       forgot-password, verify-code-success), {@code merchant} (create,
 *       update-status, document-create, document-update-status) and
 *       {@code transaction} (create);</li>
 *   <li>consumer — the {@code @KafkaListener(topics = {...})} block in
 *       {@code email-service}'s {@code EmailService}.</li>
 * </ul>
 *
 * <p>Keeping the mirror in one place means a new notification only has to be
 * registered once; {@link TopologyValidator} then fails the migration if a
 * producer starts publishing to a topic the worker does not read.
 */
public final class EmailTopics {

    /** Topics the email worker subscribes to (one per logical notification). */
    public static final List<String> ALL = List.of(
            "email-service-topic-auth-register",
            "email-service-topic-auth-forgot-password",
            "email-service-topic-auth-verify-code-success",
            "email-service-topic-merchant-create",
            "email-service-topic-merchant-update-status",
            "email-service-topic-merchant-document-create",
            "email-service-topic-merchant-document-update-status",
            "email-service-topic-transaction-create");

    private EmailTopics() {
    }
}
