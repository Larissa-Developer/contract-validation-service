package com.larissa.contracts.application.usecase;

import com.larissa.contracts.domain.model.Contract;
import com.larissa.contracts.domain.validation.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ValidateContractUseCaseTest {

    @Test
    void shouldReturnSummaryWithOverallStatusAllPassed() {

        Contract contract = new Contract(
                "CNT-1",
                "CL-1",
                "CREDIT",
                BigDecimal.valueOf(2000),
                "BRL",
                60,
                LocalDate.now().plusDays(1),
                30,
                "A",
                true,
                Map.of(
                        "annualIncome", 90000,
                        "customerAge", 30,
                        "riskTier", "LOW",
                        "collateralRequired", false
                )
        );

        ValidateContractUseCase useCase =
                new ValidateContractUseCase(List.of(
                        new AmountValidationRule(),
                        new CurrencyValidationRule(),
                        new StartDateValidationRule(),
                        new TermValidationRule(),
                        new IncomeValidationRule(),
                        new RiskTierValidationRule(),
                        new CollateralValidationRule(),
                        new AgeValidationRule()
                ));

        ContractValidationSummary summary = useCase.execute(contract);

        assertEquals("CNT-1", summary.getContractId());
        assertNotNull(summary.getTimestamp());
        assertEquals(OverallStatus.ALL_PASSED, summary.getOverallStatus());
        assertFalse(summary.getResults().isEmpty());
        assertTrue(summary.getResults().stream().allMatch(ValidationResult::isPassed));
    }
}
