package com.larissa.contracts.infrastructure.messaging.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.larissa.contracts.application.usecase.ValidateContractUseCase;
import com.larissa.contracts.domain.validation.ContractValidationSummary;
import com.larissa.contracts.infrastructure.messaging.kafka.dto.ContractMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ContractKafkaListener {

    private static final Logger log = LoggerFactory.getLogger(ContractKafkaListener.class);

    private final ObjectMapper objectMapper;
    private final ValidateContractUseCase validateContractUseCase;
    private final KafkaProducers producers;

    public ContractKafkaListener(
            ObjectMapper objectMapper,
            ValidateContractUseCase validateContractUseCase,
            KafkaProducers producers
    ) {
        this.objectMapper = objectMapper;
        this.validateContractUseCase = validateContractUseCase;
        this.producers = producers;
    }

    @KafkaListener(topics = "${app.kafka.topics.input}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) {

        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);

        try {
            ContractMessage contractMessage = objectMapper.readValue(message, ContractMessage.class);

            // logs com contexto
            if (contractMessage.contractId != null) {
                MDC.put("contractId", contractMessage.contractId);
            }

            var contract = ContractMapper.toDomain(contractMessage);

            ContractValidationSummary summary = validateContractUseCase.execute(contract);

            String payload = objectMapper.writeValueAsString(summary);

            producers.sendOutput(contract.getContractId(), payload);

            log.info("Contract validated and published to output topic");

        } catch (Exception e) {
            log.error("Invalid message. Sending to DLQ", e);

            // Se não temos contractId, mandamos "unknown"
            producers.sendDlq("unknown", message);

        } finally {
            MDC.clear();
        }
    }
}
