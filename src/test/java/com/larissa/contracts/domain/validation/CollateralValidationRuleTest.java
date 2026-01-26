package com.larissa.contracts.domain.validation;

import com.larissa.contracts.domain.model.Contract;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CollateralValidationRuleTest {

    @Test
    void shouldFailWhenAmountIsGreaterThanOneMillionAndNoCollateralProvided() {

        Contract contract = new Contract(
                "CNT-1",
                "CL-1",
                "CREDIT",
                BigDecimal.valueOf(1_500_000),
                "BRL",
                120,
                LocalDate.now().plusDays(1),
                40,
                "A",
                false,
                Map.of("annualIncome", 200_000)
        );

        CollateralValidationRule rule = new CollateralValidationRule();

        ValidationResult result = rule.validate(contract);

        assertFalse(result.isPassed());
        assertEquals("COLLATERAL_REQUIRED", result.getRuleName());
    }

    @Test
    void shouldPassWhenAmountIsLessThanOneMillionWithoutCollateral() {

        Contract contract = new Contract(
                "CNT-2",
                "CL-2",
                "CREDIT",
                BigDecimal.valueOf(500_000),
                "BRL",
                60,
                LocalDate.now().plusDays(1),
                30,
                "B",
                false,
                Map.of("annualIncome", 100_000)
        );

        CollateralValidationRule rule = new CollateralValidationRule();

        ValidationResult result = rule.validate(contract);

        assertTrue(result.isPassed());
    }
}
