package com.larissa.contracts.infrastructure.messaging.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.larissa.contracts.application.usecase.ValidateContractUseCase;
import com.larissa.contracts.domain.port.ValidationResultPersistencePort;
import com.larissa.contracts.domain.validation.ContractValidationSummary;
import com.larissa.contracts.infrastructure.messaging.kafka.dto.ContractMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class ContractKafkaListener {

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

        String correlationId = extractCorrelationId(record);
        MDC.put("correlationId", correlationId);

        ContractMessage contractMessage = objectMapper.readValue(record.value(), ContractMessage.class);

        if (contractMessage.contractId != null) {
            MDC.put("contractId", contractMessage.contractId);
        }

        var contract = ContractMapper.toDomain(contractMessage);

        ContractValidationSummary summary = validateContractUseCase.execute(contract);

        persistencePort.saveBatch(summary, correlationId);

        String payload = objectMapper.writeValueAsString(summary);
        producers.sendOutput(contract.getContractId(), payload, correlationId);

        MDC.clear();
    }

    private String extractCorrelationId(ConsumerRecord<String, String> record) {
        var header = record.headers().lastHeader("correlationId");
        if (header != null && header.value() != null && header.value().length > 0) {
            return new String(header.value(), StandardCharsets.UTF_8);
        }
        return UUID.randomUUID().toString();
    }
}
