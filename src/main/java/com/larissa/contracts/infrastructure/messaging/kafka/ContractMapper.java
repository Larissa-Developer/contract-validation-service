package com.larissa.contracts.infrastructure.messaging.kafka;

import com.larissa.contracts.domain.model.Contract;
import com.larissa.contracts.infrastructure.messaging.kafka.dto.ContractMessage;

import java.util.HashMap;
import java.util.Map;

public class ContractMapper {

    private ContractMapper() {
        // util
    }

    public static Contract toDomain(ContractMessage msg) {

        Map<String, Object> attrs = msg.attributes != null
                ? new HashMap<>(msg.attributes)
                : new HashMap<>();

        // garantir que suas rules encontrem o que precisam em attributes (se vierem fora)
        if (msg.customerAge != null) attrs.putIfAbsent("customerAge", msg.customerAge);
        if (msg.clientRiskTier != null) attrs.putIfAbsent("riskTier", msg.clientRiskTier);

        // a regra de collateral usa o boolean do próprio contrato
        // então não precisa colocar em attributes, mas não atrapalha se colocar:
        if (msg.collateralProvided != null) attrs.putIfAbsent("collateralProvided", msg.collateralProvided);

        return new Contract(
                msg.contractId,
                msg.clientId,
                msg.productType,
                msg.amount,
                msg.currency,
                msg.termMonths != null ? msg.termMonths : 0,
                msg.startDate,
                msg.customerAge != null ? msg.customerAge : 0,
                msg.clientRiskTier,
                msg.collateralProvided != null && msg.collateralProvided,
                attrs
        );
    }
}
