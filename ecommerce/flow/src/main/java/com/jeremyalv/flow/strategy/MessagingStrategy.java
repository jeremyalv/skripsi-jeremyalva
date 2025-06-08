package com.jeremyalv.flow.strategy;

import com.jeremyalv.flow.dto.analytics.MessageEnvelopeDTO;
import com.jeremyalv.flow.model.PublishResult;

import java.util.concurrent.CompletableFuture;

public interface MessagingStrategy<K, V> {
    CompletableFuture<PublishResult> publish(MessageEnvelopeDTO<K, V> message);
}
