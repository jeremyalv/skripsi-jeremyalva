package com.jeremyalv.flow.strategy;

import com.jeremyalv.flow.dto.analytics.MessageEnvelopeDTO;
import com.jeremyalv.flow.model.PublishResult;
import com.jeremyalv.flow.service.analytics.PulsarService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import java.util.concurrent.CompletableFuture;

@ConditionalOnProperty(name = "app.messaging.platform", havingValue = "pulsar")
@Slf4j
public class PulsarPublishingStrategy<K, V> implements MessagingStrategy<K, V> {

    private final PulsarService<V> pulsarService;

    public PulsarPublishingStrategy(PulsarService<V> pulsarService) {
        this.pulsarService = pulsarService;
    }

    @Override
    public CompletableFuture<PublishResult> publish(MessageEnvelopeDTO<K, V> message) {
        return pulsarService.publishEvent(message);
    }
}
