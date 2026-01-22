package com.larissa.contracts.domain.validation;

import com.larissa.contracts.domain.model.Contract;

import java.util.Set;

public class CurrencyValidationRule implements ValidationRule {

    private static final String RULE_NAME = "CURRENCY_VALIDATION";

    private static final Set<String> ALLOWED_CURRENCIES =
            Set.of("BRL", "USD", "EUR");

    @Override
    public ValidationResult validate(Contract contract) {

        long start = System.currentTimeMillis();

        String currency = contract.getCurrency();

        if (currency == null || currency.isBlank()) {
            return ValidationResult.failed(
                    contract.getContractId(),
                    RULE_NAME,
                    "Currency not informed",
                    System.currentTimeMillis() - start
            );
        }

        if (!ALLOWED_CURRENCIES.contains(currency)) {
            return ValidationResult.failed(
                    contract.getContractId(),
                    RULE_NAME,
                    "Currency not supported",
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
