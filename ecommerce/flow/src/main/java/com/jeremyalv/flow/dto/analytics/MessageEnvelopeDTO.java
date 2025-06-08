package com.jeremyalv.flow.dto.analytics;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Collections;
import java.util.Map;

@Getter
@ToString
@EqualsAndHashCode
public final class MessageEnvelopeDTO<K, V> {
    private final String topic;
    private final K key;
    private final V payload;
    private final Map<String, String> headers;

    public MessageEnvelopeDTO(@NotNull String topic, K key, V payload, Map<String, String> headers) {
        this.topic = topic;
        this.key = key;
        this.payload = payload;
        this.headers = Collections.unmodifiableMap(headers);
    }
}
