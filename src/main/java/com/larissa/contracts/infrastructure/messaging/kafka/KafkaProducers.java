package com.larissa.contracts.infrastructure.messaging.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducers {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String outputTopic;
    private final String dlqTopic;

    public KafkaProducers(
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${app.kafka.topics.output}") String outputTopic,
            @Value("${app.kafka.topics.dlq}") String dlqTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.outputTopic = outputTopic;
        this.dlqTopic = dlqTopic;
    }

    public void sendOutput(String key, String payload) {
        kafkaTemplate.send(outputTopic, key, payload);
    }

    public void sendDlq(String key, String payload) {
        kafkaTemplate.send(dlqTopic, key, payload);
    }
}
