package com.larissa.contracts.domain.validation;

import java.time.Instant;
import java.util.List;

public class ContractValidationSummary {

    private final String contractId;
    private final Instant timestamp;
    private final OverallStatus overallStatus;
    private final List<ValidationResult> results;

    public ContractValidationSummary(
            String contractId,
            Instant timestamp,
            OverallStatus overallStatus,
            List<ValidationResult> results
    ) {
        this.contractId = contractId;
        this.timestamp = timestamp;
        this.overallStatus = overallStatus;
        this.results = results;
    }

    public String getContractId() {
        return contractId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public OverallStatus getOverallStatus() {
        return overallStatus;
    }

    public List<ValidationResult> getResults() {
        return results;
    }
}
