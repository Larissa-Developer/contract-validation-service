package com.larissa.contracts.domain.port;

import com.larissa.contracts.domain.validation.ContractValidationSummary;

public interface ValidationResultPersistencePort {
    void saveBatch(ContractValidationSummary summary, String correlationId);
}
