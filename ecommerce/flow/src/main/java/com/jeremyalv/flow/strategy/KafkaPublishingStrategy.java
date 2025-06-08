package com.jeremyalv.flow.strategy;

import java.util.concurrent.CompletableFuture;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import com.jeremyalv.flow.dto.analytics.MessageEnvelopeDTO;
import com.jeremyalv.flow.model.PublishResult;
import com.jeremyalv.flow.service.analytics.KafkaService;

@ConditionalOnProperty(name = "app.messaging.platform", havingValue = "kafka", matchIfMissing = true)
public class KafkaPublishingStrategy<K, V> implements MessagingStrategy<K, V> {
    private final KafkaService<K, V> kafkaService;

    public KafkaPublishingStrategy(KafkaService<K, V> kafkaService) {
        this.kafkaService = kafkaService;
    }

    @Override
    public CompletableFuture<PublishResult> publish(MessageEnvelopeDTO<K, V> message) {
        return kafkaService.publishEvent(message);
    }
}
