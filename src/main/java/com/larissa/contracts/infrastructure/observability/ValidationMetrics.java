package com.larissa.contracts.infrastructure.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ValidationMetrics {

    private final MeterRegistry registry;

    public ValidationMetrics(MeterRegistry registry) {
        this.registry = registry;
    }

    public void incrementProcessed() {
        Counter.builder("contracts.processed.total")
                .description("Total contracts processed")
                .register(registry)
                .increment();
    }

    public void recordEndToEndMs(long ms) {
        Timer.builder("contracts.validation.end_to_end")
                .description("End-to-end processing time from consume to publish+persist")
                .publishPercentileHistogram()
                .register(registry)
                .record(Duration.ofMillis(ms));
    }

    public void recordRule(String ruleName, boolean passed, long durationMs) {
        Counter.builder("contracts.validation.rule.total")
                .description("Rule executions grouped by ruleName and result")
                .tag("rule", ruleName)
                .tag("passed", Boolean.toString(passed))
                .register(registry)
                .increment();

        Timer.builder("contracts.validation.rule.duration")
                .description("Rule execution duration")
                .tag("rule", ruleName)
                .publishPercentileHistogram()
                .register(registry)
                .record(Duration.ofMillis(durationMs));
    }
}
