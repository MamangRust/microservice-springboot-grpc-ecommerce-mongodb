package com.sanedge.ecommerce.order.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;

import com.sanedge.ecommerce.common.chaos.ChaosManager;
import com.sanedge.ecommerce.common.outbox.KafkaOutboxPublisher;
import com.sanedge.ecommerce.common.outbox.OutboxEventRepository;
import com.sanedge.ecommerce.order.models.OrderOutbox;
import com.sanedge.ecommerce.order.repository.OrderOutboxRepository;

@Component
public class OrderOutboxPublisher extends KafkaOutboxPublisher<OrderOutbox> {

    private final OrderOutboxRepository repository;

    @Value("${order.outbox.batch-size:50}")
    private int batchSize;

    @Value("${order.outbox.retry-delay-seconds:30}")
    private long retryDelaySeconds;

    @Value("${order.outbox.max-attempts:5}")
    private int maxAttempts;

    public OrderOutboxPublisher(OrderOutboxRepository repository,
            KafkaTemplate<String, String> kafkaTemplate,
            ChaosManager chaosManager,
            ObjectProvider<MeterRegistry> meterRegistry) {
        super(kafkaTemplate, chaosManager, meterRegistry);
        this.repository = repository;
    }

    @Override
    protected String moduleName() {
        return "order";
    }

    @Override
    protected OutboxEventRepository<OrderOutbox> repository() {
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
