package com.jeremyalv.flow.utils;

import java.util.UUID;

import com.jeremyalv.flow.strategy.KafkaPublishingStrategy;
import com.jeremyalv.flow.strategy.MessagingStrategy;

public class Utils {
    public static String GenerateId() {
        return UUID.randomUUID().toString();
    }
    
    public static <K, V> String DetermineTopic(MessagingStrategy<K, V> strategy, String topicName) {
        if (strategy instanceof KafkaPublishingStrategy) {
            return topicName;
        }

        return String.format("persistent://public/default/%s", topicName);
    }
}
