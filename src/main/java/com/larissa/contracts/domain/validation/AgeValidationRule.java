package com.larissa.contracts.domain.validation;

import com.larissa.contracts.domain.model.Contract;

import java.util.Map;

public class AgeValidationRule implements ValidationRule {

    private static final String RULE_NAME = "AGE_VALIDATION";
    private static final int MIN_AGE = 18;
    private static final int MAX_AGE = 65;

    @Override
    public ValidationResult validate(Contract contract) {

        long start = System.currentTimeMillis();

        Map<String, Object> attributes = contract.getAttributes();

        Object ageObj = attributes.get("customerAge");

        if (ageObj == null) {
            return ValidationResult.failed(
                    contract.getContractId(),
                    RULE_NAME,
                    "Customer age not informed",
                    System.currentTimeMillis() - start
            );
        }

        int age = (int) ageObj;

        if (age < MIN_AGE || age > MAX_AGE) {
            return ValidationResult.failed(
                    contract.getContractId(),
                    RULE_NAME,
                    "Customer age out of allowed range",
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
