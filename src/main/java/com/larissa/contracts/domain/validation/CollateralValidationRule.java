package com.larissa.contracts.domain.validation;

import com.larissa.contracts.domain.model.Contract;

import java.math.BigDecimal;

public class CollateralValidationRule implements ValidationRule {

    private static final String RULE_NAME = "COLLATERAL_REQUIRED";
    private static final BigDecimal LIMIT = BigDecimal.valueOf(1_000_000);

    @Override
    public ValidationResult validate(Contract contract) {

        long start = System.currentTimeMillis();

        BigDecimal amount = contract.getAmount();
        boolean collateralProvided = contract.isCollateralProvided();

        if (amount.compareTo(LIMIT) <= 0) {
            return ValidationResult.passed(
                    contract.getContractId(),
                    RULE_NAME,
                    System.currentTimeMillis() - start
            );
        }

        if (collateralProvided) {
            return ValidationResult.passed(
                    contract.getContractId(),
                    RULE_NAME,
                    System.currentTimeMillis() - start
            );
        }

        return ValidationResult.failed(
                contract.getContractId(),
                RULE_NAME,
                "A garantia é necessária para contratos acima de 1.000.000",
                System.currentTimeMillis() - start
        );
    }
}
