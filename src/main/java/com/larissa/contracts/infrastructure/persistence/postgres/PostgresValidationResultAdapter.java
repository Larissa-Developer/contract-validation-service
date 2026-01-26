package com.larissa.contracts.infrastructure.persistence.postgres;

import com.larissa.contracts.domain.port.ValidationResultPersistencePort;
import com.larissa.contracts.domain.validation.ContractValidationSummary;
import com.larissa.contracts.domain.validation.ValidationResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class PostgresValidationResultAdapter implements ValidationResultPersistencePort {

    private final JdbcTemplate jdbcTemplate;

    public PostgresValidationResultAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void saveBatch(ContractValidationSummary summary, String correlationId) {
        List<ValidationResult> results = summary.getResults();

        // batch 10-50 - 20 como default.
        int batchSize = 20;

        String sql = """
            INSERT INTO contract_validation_results
              (contract_id, rule_name, passed, message, duration_ms, correlation_id)
            VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT (contract_id, rule_name) DO NOTHING
            """;

        for (int i = 0; i < results.size(); i += batchSize) {
            List<ValidationResult> batch = results.subList(i, Math.min(i + batchSize, results.size()));

            jdbcTemplate.batchUpdate(sql, batch, batch.size(), (ps, r) -> {
                ps.setString(1, r.getContractId());
                ps.setString(2, r.getRuleName());
                ps.setBoolean(3, r.isPassed());
                ps.setString(4, r.getMessage());
                ps.setLong(5, r.getDurationMs());
                ps.setString(6, correlationId);
            });
        }
    }
}
