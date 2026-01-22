package com.larissa.contracts.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class Contract {

    private final String contractId;
    private final String clientId;
    private final String productType;
    private final BigDecimal amount;
    private final String currency;
    private final int termMonths;
    private final LocalDate startDate;
    private final int customerAge;
    private final String clientRiskTier;
    private final boolean collateralProvided;
    private final Map<String, Object> attributes;

    public Contract(
            String contractId,
            String clientId,
            String productType,
            BigDecimal amount,
            String currency,
            int termMonths,
            LocalDate startDate,
            int customerAge,
            String clientRiskTier,
            boolean collateralProvided,
            Map<String, Object> attributes
    ) {
        this.contractId = contractId;
        this.clientId = clientId;
        this.productType = productType;
        this.amount = amount;
        this.currency = currency;
        this.termMonths = termMonths;
        this.startDate = startDate;
        this.customerAge = customerAge;
        this.clientRiskTier = clientRiskTier;
        this.collateralProvided = collateralProvided;
        this.attributes = attributes;
    }

    public String getContractId() {
        return contractId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getProductType() {
        return productType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public int getTermMonths() {
        return termMonths;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public int getCustomerAge() {
        return customerAge;
    }

    public String getClientRiskTier() {
        return clientRiskTier;
    }

    public boolean isCollateralProvided() {
        return collateralProvided;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
