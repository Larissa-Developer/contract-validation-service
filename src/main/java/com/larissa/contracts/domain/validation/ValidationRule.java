package com.larissa.contracts.domain.validation;


import com.larissa.contracts.domain.model.Contract;

public interface ValidationRule {
    ValidationResult validate(Contract contract);
}
