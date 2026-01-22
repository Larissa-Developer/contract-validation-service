package com.larissa.contracts.domain.validation;

import com.larissa.contracts.domain.model.Contract;

import java.math.BigDecimal;
import java.util.Map;

public class IncomeValidationRule implements ValidationRule {

    private static final String RULE_NAME = "INCOME_VALIDATION";
    private static final String KEY = "annualIncome";
    private static final BigDecimal MIN_INCOME = BigDecimal.valueOf(50_000);

    @Override
    public ValidationResult validate(Contract contract) {

        long start = System.currentTimeMillis();

        Map<String, Object> attributes = contract.getAttributes();

        if (attributes == null) {
            return ValidationResult.failed(
                    contract.getContractId(),
                    RULE_NAME,
                    "Attributes not informed",
                    System.currentTimeMillis() - start
            );
        }

        Object incomeObj = attributes.get(KEY);

        if (incomeObj == null) {
            return ValidationResult.failed(
                    contract.getContractId(),
                    RULE_NAME,
                    "Annual income not informed",
                    System.currentTimeMillis() - start
            );
        }

        BigDecimal income;

        try {
            // aceita Integer, Long, Double, String...
            income = new BigDecimal(incomeObj.toString());
        } catch (Exception e) {
            return ValidationResult.failed(
                    contract.getContractId(),
                    RULE_NAME,
                    "Invalid annual income value",
                    System.currentTimeMillis() - start
            );
        }

        if (income.compareTo(MIN_INCOME) < 0) {
            return ValidationResult.failed(
                    contract.getContractId(),
                    RULE_NAME,
                    "Annual income below minimum required: " + MIN_INCOME,
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
