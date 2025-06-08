package com.jeremyalv.flow.service.analytics;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.jeremyalv.flow.dto.analytics.MessageEnvelopeDTO;
import com.jeremyalv.flow.model.PublishResult;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

@Service
@ConditionalOnProperty(name = "app.messaging.platform", havingValue = "kafka", matchIfMissing = true)
@Slf4j
public class KafkaService<K, V> {
    private final KafkaProducer<K, V> producer;

    @Autowired
    public KafkaService(KafkaProducer<K, V> producer) {
        this.producer = producer;
    }

    /**
     * Publishes an event asynchronously to Kafka.
     */
    public CompletableFuture<PublishResult> publishEvent(MessageEnvelopeDTO<K, V> eventDto) {
        CompletableFuture<PublishResult> futureResult = new CompletableFuture<>();

        Iterable<Header> kafkaHeaders = null;
        if (eventDto.getHeaders() != null && !eventDto.getHeaders().isEmpty()) {
            List<Header> headerList = new ArrayList<>();
            eventDto.getHeaders().forEach((key, value) ->
                    headerList.add(new RecordHeader(key, value.getBytes(StandardCharsets.UTF_8)))
            );
            kafkaHeaders = headerList;
        }

        ProducerRecord<K,V> record = new ProducerRecord<>(
                eventDto.getTopic(),
                null,
                eventDto.getKey(),
                eventDto.getPayload(),
                kafkaHeaders
        );

        producer.send(record, (recordMetadata, exception) -> {
            if (exception == null) {
                futureResult.complete(PublishResult.success(recordMetadata));
            } else {
                log.error("Failed to publish message to Kafka topic [{}], key [{}]: {}",
                        eventDto.getTopic(), eventDto.getKey(), exception.getMessage(), exception);

                futureResult.complete(PublishResult.failure(exception));
            }
        });

        return futureResult;
    }

    @PreDestroy
    public void closeProducer() {
        log.info("Closing Kafka producer.");
        if (producer != null) {
            try {
                producer.close();
            } catch (KafkaException e) {
                log.error("Error closing Kafka producer: {}", e.getMessage(), e);
            }
        }
    }
}
