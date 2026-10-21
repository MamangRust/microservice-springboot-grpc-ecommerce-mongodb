package com.sanedge.ecommerce.transaction.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;

import com.sanedge.ecommerce.common.chaos.ChaosManager;
import com.sanedge.ecommerce.common.outbox.KafkaOutboxPublisher;
import com.sanedge.ecommerce.common.outbox.OutboxEventRepository;
import com.sanedge.ecommerce.transaction.models.TransactionOutbox;
import com.sanedge.ecommerce.transaction.repository.TransactionOutboxRepository;


@Component
public class TransactionOutboxPublisher extends KafkaOutboxPublisher<TransactionOutbox> {

    private final TransactionOutboxRepository repository;

    @Value("${transaction.outbox.batch-size:50}")
    private int batchSize;

    @Value("${transaction.outbox.retry-delay-seconds:30}")
    private long retryDelaySeconds;

    @Value("${transaction.outbox.max-attempts:5}")
    private int maxAttempts;

    public TransactionOutboxPublisher(TransactionOutboxRepository repository,
            KafkaTemplate<String, String> kafkaTemplate,
            ChaosManager chaosManager,
            ObjectProvider<MeterRegistry> meterRegistry) {
        super(kafkaTemplate, chaosManager, meterRegistry);
        this.repository = repository;
    }

    @Override
    protected String moduleName() {
        return "transaction";
    }

    @Override
    protected OutboxEventRepository<TransactionOutbox> repository() {
        return repository;
    }

    @Override
    protected int batchSize() {
        return batchSize;
    }

    @Override
    protected long retryDelaySeconds() {
        return retryDelaySeconds;
    }

    @Override
    protected int maxAttempts() {
        return maxAttempts;
    }
}
