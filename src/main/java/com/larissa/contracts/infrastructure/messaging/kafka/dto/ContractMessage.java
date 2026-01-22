package com.larissa.contracts.infrastructure.messaging.kafka.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContractMessage {

    public String contractId;
    public String clientId;
    public String productType;
    public BigDecimal amount;
    public String currency;
    public Integer termMonths;
    public LocalDate startDate;
    public Integer customerAge;
    public String clientRiskTier;
    public Boolean collateralProvided;
    public Map<String, Object> attributes;
}
