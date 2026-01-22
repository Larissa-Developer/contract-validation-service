package com.larissa.contracts.application.usecase;

import com.larissa.contracts.domain.model.Contract;
import com.larissa.contracts.domain.validation.ContractValidationSummary;
import com.larissa.contracts.domain.validation.OverallStatus;
import com.larissa.contracts.domain.validation.ValidationResult;
import com.larissa.contracts.domain.validation.ValidationRule;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ValidateContractUseCase {

    private final List<ValidationRule> validationRules;

    public ValidateContractUseCase(List<ValidationRule> validationRules) {
        this.validationRules = validationRules;
    }

    public ContractValidationSummary execute(Contract contract) {

        List<CompletableFuture<ValidationResult>> futures =
                validationRules.stream()
                        .map(rule -> CompletableFuture.supplyAsync(() -> rule.validate(contract)))
                        .toList();

        List<ValidationResult> results =
                futures.stream()
                        .map(CompletableFuture::join)
                        .toList();

        boolean allPassed = results.stream().allMatch(ValidationResult::isPassed);

        OverallStatus status = allPassed
                ? OverallStatus.ALL_PASSED
                : OverallStatus.SOME_FAILED;

        return new ContractValidationSummary(
                contract.getContractId(),
                Instant.now(),
                status,
                results
        );
    }
}
