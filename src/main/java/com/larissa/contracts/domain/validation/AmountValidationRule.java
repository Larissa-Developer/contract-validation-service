package com.larissa.contracts.domain.validation;

import com.larissa.contracts.domain.model.Contract;

import java.math.BigDecimal;

public class AmountValidationRule implements ValidationRule {

    private static final String RULE_NAME = "AMOUNT_VALIDATION";

    @Override
    public ValidationResult validate(Contract contract) {

        long start = System.currentTimeMillis();

        BigDecimal amount = contract.getAmount();

        if (amount == null) {
            return ValidationResult.failed(
                    contract.getContractId(),
                    RULE_NAME,
                    "Valor do contrato não informado",
                    System.currentTimeMillis() - start
            );
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ValidationResult.failed(
                    contract.getContractId(),
                    RULE_NAME,
                    "O valor do contrato deve ser maior que zero",
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
