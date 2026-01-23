package com.larissa.contracts.infrastructure.messaging.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.larissa.contracts.application.usecase.ValidateContractUseCase;
import com.larissa.contracts.domain.port.ValidationResultPersistencePort;
import com.larissa.contracts.domain.validation.ContractValidationSummary;
import com.larissa.contracts.infrastructure.messaging.kafka.dto.ContractMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Component
public class ContractKafkaListener {

    private static final Logger log = LoggerFactory.getLogger(ContractKafkaListener.class);

    private static final String CORRELATION_HEADER = "correlationId";

    private final ObjectMapper objectMapper;
    private final ValidateContractUseCase validateContractUseCase;
    private final ValidationResultPersistencePort persistencePort;
    private final KafkaProducers producers;

    public ContractKafkaListener(
            ObjectMapper objectMapper,
            ValidateContractUseCase validateContractUseCase,
            ValidationResultPersistencePort persistencePort,
            KafkaProducers producers
    ) {
        this.objectMapper = objectMapper;
        this.validateContractUseCase = validateContractUseCase;
        this.persistencePort = persistencePort;
        this.producers = producers;
    }

    @KafkaListener(topics = "${app.kafka.topics.input}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, String> record) throws Exception {
        Instant start = Instant.now();

        String correlationId = extractCorrelationId(record);

        MDC.put("correlationId", correlationId);
        MDC.put("kafkaTopic", record.topic());
        MDC.put("kafkaPartition", String.valueOf(record.partition()));
        MDC.put("kafkaOffset", String.valueOf(record.offset()));

        try {
            String raw = record.value();
            if (raw == null || raw.isBlank()) {
                log.warn("Received empty payload");
                throw new IllegalArgumentException("Empty Kafka message payload");
            }

            ContractMessage contractMessage = objectMapper.readValue(raw, ContractMessage.class);

            if (contractMessage != null && contractMessage.contractId != null && !contractMessage.contractId.isBlank()) {
                MDC.put("contractId", contractMessage.contractId);
            }

            log.info("Contract message received for processing");

            var contract = ContractMapper.toDomain(contractMessage);

            ContractValidationSummary summary = validateContractUseCase.execute(contract);

            persistencePort.saveBatch(summary, correlationId);

            String payload = objectMapper.writeValueAsString(summary);
            producers.sendOutput(contract.getContractId(), payload, correlationId);

            long ms = Duration.between(start, Instant.now()).toMillis();
            MDC.put("durationMs", String.valueOf(ms));
            log.info("Contract processed successfully");

        } finally {
            MDC.clear();
        }
    }

    private String extractCorrelationId(ConsumerRecord<String, String> record) {
        var header = record.headers().lastHeader(CORRELATION_HEADER);
        if (header != null && header.value() != null && header.value().length > 0) {
            return new String(header.value(), StandardCharsets.UTF_8);
        }
        return UUID.randomUUID().toString();
    }
}
