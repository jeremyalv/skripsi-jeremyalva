package com.jeremyalv.flow.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jeremyalv.flow.model.User;
import com.jeremyalv.flow.service.analytics.KafkaService;

@Configuration
@ConditionalOnProperty(name = "app.messaging.platform", havingValue = "kafka", matchIfMissing = true)
public class KafkaProducerConfig {
    private final KafkaProperties kafkaProperties;

    public KafkaProducerConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public KafkaProducer<String, User> kafkaUserProducer() {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildProducerProperties());
        return new KafkaProducer<>(props);
    }

    @Bean
    public KafkaService<String, User> kafkaUserService(KafkaProducer<String, User> kafkaUserProducer) {
        return new KafkaService<>(kafkaUserProducer);
    }
}
