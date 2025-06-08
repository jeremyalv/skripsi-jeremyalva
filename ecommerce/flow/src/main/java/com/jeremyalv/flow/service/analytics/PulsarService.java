package com.jeremyalv.flow.service.analytics;

import java.util.concurrent.CompletableFuture;

import org.apache.pulsar.client.api.MessageId;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.pulsar.core.PulsarOperations.SendMessageBuilder;
import org.springframework.pulsar.core.PulsarTemplate;
import org.springframework.stereotype.Service;

import com.jeremyalv.flow.dto.analytics.MessageEnvelopeDTO;
import com.jeremyalv.flow.model.PublishResult;

import lombok.extern.slf4j.Slf4j;

@Service
@ConditionalOnProperty(name = "app.messaging.platform", havingValue = "pulsar")
@Slf4j
public class PulsarService<V> {
    private final PulsarTemplate<V> pulsarTemplate;

    public PulsarService(PulsarTemplate<V> pulsarTemplate) {
        this.pulsarTemplate = pulsarTemplate;
    }

    /**
     * Publishes an event asynchronously to Pulsar.
     */
    public CompletableFuture<PublishResult> publishEvent(MessageEnvelopeDTO<?, V> eventDto) {
        CompletableFuture<PublishResult> futureResult = new CompletableFuture<>();
        String destinationTopic = eventDto.getTopic();

        try {
            SendMessageBuilder<V> messageBuilder = pulsarTemplate.newMessage(eventDto.getPayload());

            messageBuilder.withTopic(destinationTopic);

            if (eventDto.getHeaders() != null && !eventDto.getHeaders().isEmpty()) {
                messageBuilder.withMessageCustomizer(typedMessageBuilder -> {
                    eventDto.getHeaders().forEach(typedMessageBuilder::property);
                });
            }

            CompletableFuture<MessageId> sendFuture = messageBuilder.sendAsync();
            sendFuture.whenComplete((messageId, exception) -> {
                if (exception == null) {
                    futureResult.complete(PublishResult.success(messageId.toString()));
                } else {
                    log.error("Failed to publish message to Pulsar topic [{}], key [{}]: {}",
                            eventDto.getTopic(), eventDto.getKey(), exception.getMessage(), exception);

                    futureResult.complete(PublishResult.failure(exception));
                }
            });

        } catch (Exception e) {
            log.error("Error preparing message for Pulsar topic [{}], key [{}]: {}",
                    eventDto.getTopic(), eventDto.getKey(), e.getMessage(), e);

            futureResult.complete(PublishResult.failure(e));
        }

        return futureResult;
    }
}
