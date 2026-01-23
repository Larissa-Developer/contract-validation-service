package com.larissa.contracts.infrastructure.messaging.kafka;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class KafkaProducers {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.kafka.topics.output}")
    private String outputTopic;

    @Value("${app.kafka.topics.dlq}")
    private String dlqTopic;

    public KafkaProducers(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOutput(String key, String payload, String correlationId) {
        ProducerRecord<String, String> record = new ProducerRecord<>(outputTopic, key, payload);
        record.headers().add(new RecordHeader("correlationId", correlationId.getBytes(StandardCharsets.UTF_8)));
        kafkaTemplate.send(record);
    }

    public void sendDlq(String key, String payload, String correlationId) {
        ProducerRecord<String, String> record = new ProducerRecord<>(dlqTopic, key, payload);
        record.headers().add(new RecordHeader("correlationId", correlationId.getBytes(StandardCharsets.UTF_8)));
        kafkaTemplate.send(record);
    }

    public String dlqTopic() {
        return dlqTopic;
    }
}
