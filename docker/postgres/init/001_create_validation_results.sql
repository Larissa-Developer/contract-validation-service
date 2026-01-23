-- Criação da tabela de resultados de validação de contratos
-- Executado automaticamente pelo Postgres ao subir o container
-- via /docker-entrypoint-initdb.d

CREATE TABLE IF NOT EXISTS contract_validation_results (
                                                           id BIGSERIAL PRIMARY KEY,

                                                           contract_id VARCHAR(64) NOT NULL,
    rule_name VARCHAR(100) NOT NULL,

    passed BOOLEAN NOT NULL,
    message TEXT,
    duration_ms BIGINT NOT NULL,

    correlation_id VARCHAR(64),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

-- ------------------------------------------------------------------
-- Idempotência:
-- Garante que uma mesma regra para o mesmo contrato
-- não seja persistida mais de uma vez,
-- mesmo em caso de reprocessamento Kafka ou retry
-- ------------------------------------------------------------------
CREATE UNIQUE INDEX IF NOT EXISTS ux_contract_rule
    ON contract_validation_results (contract_id, rule_name);

-- ------------------------------------------------------------------
-- Índices auxiliares (opcional, mas bom para observabilidade/debug)
-- ------------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_contract_id
    ON contract_validation_results (contract_id);

CREATE INDEX IF NOT EXISTS idx_created_at
    ON contract_validation_results (created_at);
