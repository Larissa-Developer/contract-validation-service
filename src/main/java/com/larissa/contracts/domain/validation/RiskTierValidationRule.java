package com.larissa.contracts.domain.validation;

import com.larissa.contracts.domain.model.Contract;

import java.util.Map;
import java.util.Set;

public class RiskTierValidationRule implements ValidationRule {

    private static final String RULE_NAME = "RISK_TIER_VALIDATION";
    private static final String RISK_TIER_KEY = "riskTier";

    private static final Set<String> ALLOWED_RISK_TIERS =
            Set.of("LOW", "MEDIUM", "HIGH");

    @Override
    public ValidationResult validate(Contract contract) {

        long start = System.currentTimeMillis();

        Map<String, Object> attributes = contract.getAttributes();

        if (attributes == null || !attributes.containsKey(RISK_TIER_KEY)) {
            return ValidationResult.failed(
                    contract.getContractId(),
                    RULE_NAME,
                    "Nível de risco não informado",
                    System.currentTimeMillis() - start
            );
        }

        String riskTier = attributes.get(RISK_TIER_KEY).toString().toUpperCase();

        if (!ALLOWED_RISK_TIERS.contains(riskTier)) {
            return ValidationResult.failed(
                    contract.getContractId(),
                    RULE_NAME,
                    "Nível de risco inválido: " + riskTier,
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
