package com.jeremyalv.flow.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jeremyalv.flow.model.Order;
import com.jeremyalv.flow.model.Product;
import com.jeremyalv.flow.model.User;
import com.jeremyalv.flow.service.analytics.KafkaService;
import com.jeremyalv.flow.service.analytics.PulsarService;
import com.jeremyalv.flow.strategy.KafkaPublishingStrategy;
import com.jeremyalv.flow.strategy.MessagingStrategy;
import com.jeremyalv.flow.strategy.PulsarPublishingStrategy;

@Configuration
public class MessagingStrategyConfig {
    // --- Kafka Strategy Beans ---
    @Bean
    @ConditionalOnProperty(name = "app.messaging.platform", havingValue = "kafka", matchIfMissing = true)
    public MessagingStrategy<String, User> kafkaAuthMessagingStrategy(KafkaService<String, User> kafkaService) {
        return new KafkaPublishingStrategy<>(kafkaService);
    }

    @Bean
    @ConditionalOnProperty(name = "app.messaging.platform", havingValue = "kafka", matchIfMissing = true)
    public MessagingStrategy<String, Product> kafkaProductMessagingStrategy(
            KafkaService<String, Product> kafkaService) {
        return new KafkaPublishingStrategy<>(kafkaService);
    }

    @Bean
    @ConditionalOnProperty(name = "app.messaging.platform", havingValue = "kafka", matchIfMissing = true)
    public MessagingStrategy<String, Order> kafkaOrderMessagingStrategy(
        KafkaService<String, Order> kafkaService) {
        return new KafkaPublishingStrategy<>(kafkaService);
    }


    // --- Pulsar Strategy Beans ---
    @Bean
    @ConditionalOnProperty(name = "app.messaging.platform", havingValue = "pulsar")
    public MessagingStrategy<String, User> pulsarAuthMessagingStrategy(PulsarService<User> pulsarService) {
        return new PulsarPublishingStrategy<>(pulsarService);
    }

    @Bean
    @ConditionalOnProperty(name = "app.messaging.platform", havingValue = "pulsar")
    public MessagingStrategy<String, Product> pulsarProductMessagingStrategy(
        PulsarService<Product> pulsarService) {
        return new PulsarPublishingStrategy<>(pulsarService);
    }

    @Bean
    @ConditionalOnProperty(name = "app.messaging.platform", havingValue = "pulsar")
    public MessagingStrategy<String, Order> pulsarOrderMessagingStrategy(
        PulsarService<Order> pulsarService) {
        return new PulsarPublishingStrategy<>(pulsarService);
    }
}
