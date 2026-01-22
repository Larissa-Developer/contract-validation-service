package com.larissa.contracts.infrastructure.config;

import com.larissa.contracts.application.usecase.ValidateContractUseCase;
import com.larissa.contracts.domain.validation.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class UseCaseConfig {

    @Bean
    public ValidateContractUseCase validateContractUseCase() {

        List<ValidationRule> rules = List.of(
                new AmountValidationRule(),
                new CurrencyValidationRule(),
                new TermValidationRule(),
                new StartDateValidationRule(),
                new AgeValidationRule(),
                new RiskTierValidationRule(),
                new CollateralValidationRule(),
                new IncomeValidationRule()
        );

        return new ValidateContractUseCase(rules);
    }
}


