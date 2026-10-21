package com.sanedge.ecommerce.merchant.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;

import com.sanedge.ecommerce.common.chaos.ChaosManager;
import com.sanedge.ecommerce.common.outbox.KafkaOutboxPublisher;
import com.sanedge.ecommerce.common.outbox.OutboxEventRepository;
import com.sanedge.ecommerce.merchant.models.MerchantOutbox;
import com.sanedge.ecommerce.merchant.repository.MerchantOutboxRepository;

@Component
public class MerchantOutboxPublisher extends KafkaOutboxPublisher<MerchantOutbox> {

    private final MerchantOutboxRepository repository;

    @Value("${merchant.outbox.batch-size:50}")
    private int batchSize;

    @Value("${merchant.outbox.retry-delay-seconds:30}")
    private long retryDelaySeconds;

    @Value("${merchant.outbox.max-attempts:5}")
    private int maxAttempts;

    public MerchantOutboxPublisher(MerchantOutboxRepository repository,
            KafkaTemplate<String, String> kafkaTemplate,
            ChaosManager chaosManager,
            ObjectProvider<MeterRegistry> meterRegistry) {
        super(kafkaTemplate, chaosManager, meterRegistry);
        this.repository = repository;
    }

    @Override
    protected String moduleName() {
        return "merchant";
    }

    @Override
    protected OutboxEventRepository<MerchantOutbox> repository() {
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
