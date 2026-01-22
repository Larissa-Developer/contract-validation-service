package com.larissa.contracts.domain.validation;

import com.larissa.contracts.domain.model.Contract;

import java.time.LocalDate;

public class StartDateValidationRule implements ValidationRule {

    private static final String RULE_NAME = "START_DATE_VALIDATION";

    @Override
    public ValidationResult validate(Contract contract) {

        long start = System.currentTimeMillis();

        LocalDate startDate = contract.getStartDate();

        if (startDate == null) {
            return ValidationResult.failed(
                    contract.getContractId(),
                    RULE_NAME,
                    "Start date is required",
                    System.currentTimeMillis() - start
            );
        }

        if (startDate.isBefore(LocalDate.now())) {
            return ValidationResult.failed(
                    contract.getContractId(),
                    RULE_NAME,
                    "Start date cannot be in the past",
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
