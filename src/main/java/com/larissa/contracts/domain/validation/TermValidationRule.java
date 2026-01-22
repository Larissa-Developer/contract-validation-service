package com.larissa.contracts.domain.validation;

import com.larissa.contracts.domain.model.Contract;

public class TermValidationRule implements ValidationRule {

    private static final String RULE_NAME = "TERM_VALIDATION";

    @Override
    public ValidationResult validate(Contract contract) {

        long start = System.currentTimeMillis();

        Integer termMonths = contract.getTermMonths();

        if (termMonths == null) {
            return ValidationResult.failed(
                    contract.getContractId(),
                    RULE_NAME,
                    "Term not informed",
                    System.currentTimeMillis() - start
            );
        }

        if (termMonths <= 0) {
            return ValidationResult.failed(
                    contract.getContractId(),
                    RULE_NAME,
                    "Term must be greater than zero",
                    System.currentTimeMillis() - start
            );
        }

        if (termMonths > 360) {
            return ValidationResult.failed(
                    contract.getContractId(),
                    RULE_NAME,
                    "Term exceeds maximum allowed",
                    System.currentTimeMillis() - start
            );
        }

        return ValidationResult.passed(
                contract.getContractId(),
                RULE_NAME,
                System.currentTimeMillis() - start
        );
    }
}
