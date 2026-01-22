package com.larissa.contracts.domain.validation;

public class ValidationResult {

    private final String contractId;
    private final String ruleName;
    private final boolean passed;
    private final String message;
    private final long durationMs;

    private ValidationResult(
            String contractId,
            String ruleName,
            boolean passed,
            String message,
            long durationMs
    ) {
        this.contractId = contractId;
        this.ruleName = ruleName;
        this.passed = passed;
        this.message = message;
        this.durationMs = durationMs;
    }

    public static ValidationResult passed(String contractId, String ruleName, long durationMs) {
        return new ValidationResult(
                contractId,
                ruleName,
                true,
                "Validation passed",
                durationMs
        );
    }

    public static ValidationResult failed(String contractId, String ruleName, String message, long durationMs) {
        return new ValidationResult(
                contractId,
                ruleName,
                false,
                message,
                durationMs
        );
    }

    public String getContractId() {
        return contractId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public boolean isPassed() {
        return passed;
    }

    public String getMessage() {
        return message;
    }

    public long getDurationMs() {
        return durationMs;
    }
}
