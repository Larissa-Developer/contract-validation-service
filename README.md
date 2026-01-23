# Contract Validation Service
**Kafka · Parallel Validation · Persistence · DLQ · Observability**

Serviço Java/Spring Boot para validação **assíncrona e paralela** de contratos financeiros em alto volume, utilizando **Apache Kafka**, **arquitetura hexagonal** e **processamento resiliente**.

---

##  Objetivo

Processar contratos financeiros de forma eficiente e escalável, aplicando **múltiplas regras de validação em paralelo**, persistindo os resultados e publicando o status consolidado para consumo por outros sistemas.

---

##  Visão geral do fluxo

Kafka (contracts-input)

ContractKafkaListener

ValidateContractUseCase

Validações paralelas (CompletableFuture)

Persistência (PostgreSQL - batch)

Kafka (contracts-validation-results)

Erros → Kafka DLQ (contracts-input-dlq)


### Passo a passo

1. **Consumer Kafka** consome mensagens do tópico `contracts-input`
2. Payload JSON é desserializado para objeto de domínio `Contract`
3. Regras de validação são executadas **em paralelo**
4. Resultados são consolidados (`ALL_PASSED` ou `SOME_FAILED`)
5. Resultados individuais são **persistidos em batch** no PostgreSQL
6. Resultado consolidado é publicado em `contracts-validation-results`
7. Mensagens inválidas ou falhas irrecuperáveis vão para `contracts-input-dlq`

---

##  Arquitetura

Arquitetura **Hexagonal simplificada (Ports & Adapters)**.

### Domain
- Entidades: `Contract`, `ValidationResult`
- Interface de regra: `ValidationRule`
- Modelos de domínio independentes de infraestrutura

### Application
- `ValidateContractUseCase`
- Orquestração das validações paralelas
- Consolidação do resultado final

### Infrastructure
- Kafka Consumer / Producer
- Persistência PostgreSQL
- Configurações Spring, Kafka e Observabilidade

> As regras de negócio não dependem de Kafka, banco ou Spring.

---

##  Regras de validação implementadas

Executadas **em paralelo** e de forma independente:

1. Valor entre **1.000 e 5.000.000**
2. Moeda suportada: **BRL, USD, EUR**
3. Prazo entre **12 e 360 meses**
4. Data de início não pode estar no passado
5. Idade do cliente entre **18 e 75**
6. Tier de risco válido: **A, B, C, D**
7. Garantia obrigatória para valores > **1.000.000**
8. Renda anual mínima conforme o produto

Cada regra retorna:
- `contractId`
- `ruleName`
- `passed`
- `message`
- `durationMs`
- `createdAt`

---

##  Persistência

- Banco: **PostgreSQL**
- Tabela: `contract_validation_results`
- Inserções realizadas em **batch**
- Persistência ocorre antes da publicação no Kafka
- Script SQL executado automaticamente via Docker

```sql
CREATE TABLE contract_validation_results (
  id BIGSERIAL PRIMARY KEY,
  contract_id VARCHAR(64) NOT NULL,
  rule_name VARCHAR(80) NOT NULL,
  passed BOOLEAN NOT NULL,
  message TEXT,
  duration_ms BIGINT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  correlation_id VARCHAR(64)
);


Resiliência e DLQ
Retry automático para falhas transitórias

Após exceder tentativas -> mensagem enviada para DLQ

Tópico DLQ: contracts-input-dlq


Utilizado para:

JSON inválido

Payload incompleto

Erros de parsing


Observabilidade:
Actuator
GET /actuator/health

GET /actuator/health/liveness

GET /actuator/health/readiness

Métricas:
GET /actuator/metrics

GET /actuator/prometheus

Logs estruturados incluem:

contractId

correlationId

Status do processamento


Testes:

Testes unitários com JUnit 5 + Mockito

Foco em Domain e Application

Cobertura com JaCoCo

---

mvn test
mvn clean test jacoco:report
Relatório:

target/site/jacoco/index.html

-Infraestrutura com Docker
Subir serviços
docker compose up -d
docker ps

Serviços:

Kafka

Zookeeper

PostgreSQL

Kafka - Criação dos tópicos
docker exec -it kafka bash -lc "kafka-topics --bootstrap-server kafka:29092 --create --if-not-exists --topic contracts-input --partitions 3 --replication-factor 1"

docker exec -it kafka bash -lc "kafka-topics --bootstrap-server kafka:29092 --create --if-not-exists --topic contracts-validation-results --partitions 3 --replication-factor 1"

docker exec -it kafka bash -lc "kafka-topics --bootstrap-server kafka:29092 --create --if-not-exists --topic contracts-input-dlq --partitions 3 --replication-factor 1"

Executar a aplicação
mvn spring-boot:run
Enviar contrato (Producer)
docker exec -it kafka bash -lc "kafka-console-producer --bootstrap-server kafka:29092 --topic contracts-input"
Exemplo:

{"contractId":"CNT-2026-0001","clientId":"CL-12345","productType":"CREDIT","amount":250000.00,"currency":"BRL","termMonths":60,"startDate":"2026-02-15","customerAge":35,"clientRiskTier":"A","collateralProvided":true,"attributes":{"annualIncome":90000}}

Consumir resultado
docker exec -it kafka bash -lc "kafka-console-consumer --bootstrap-server kafka:29092 --topic contracts-validation-results --from-beginning"

Testar DLQ
Producer:

{"contractId":
Consumer DLQ:

docker exec -it kafka bash -lc "kafka-console-consumer --bootstrap-server kafka:29092 --topic contracts-input-dlq --from-beginning"
