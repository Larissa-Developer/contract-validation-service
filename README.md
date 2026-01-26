# Contract Validation Service
**Kafka • Parallel Processing • PostgreSQL • Observability**

Serviço Java/Spring Boot para validação de contratos financeiros em alto volume, com **processamento assíncrono**, **validações em paralelo**, **mensageria com Kafka**, **persistência em PostgreSQL** e **observabilidade básica**.

Este projeto foi desenvolvido como resposta a um **case técnico**, seguindo arquitetura limpa (hexagonal) e boas práticas de engenharia de software.

---

##  Visão Geral

Fluxo principal do sistema:

1. Consome contratos do tópico Kafka `contracts-input`
2. Valida o payload e converte para o domínio
3. Executa regras de validação **em paralelo**
4. Consolida o resultado (`ALL_PASSED` / `SOME_FAILED`)
5. Persiste os resultados no PostgreSQL (batch)
6. Publica o resultado no tópico `contracts-validation-results`
7. Mensagens inválidas são enviadas para a **DLQ**

 Diagrama do fluxo:  
`docs/diagrams/contract-validation-flow.png`

---

##  Arquitetura

Arquitetura **hexagonal simplificada (Ports & Adapters)**, com clara separação de responsabilidades.

###  Estrutura de pacotes

src/main/java/com/larissa/contracts

├── application
│ └── usecase
│ └── ValidateContractUseCase
├── domain
│ ├── model
│ ├── validation
│ └── port
│ └── ValidationResultPersistencePort
├── infrastructure
│ ├── messaging
│ │ └── kafka
│ └── persistence
│ └── postgres



### Camadas

- **Domain**
    - Entidades de negócio
    - Regras de validação
    - Portas (interfaces)

- **Application**
    - Casos de uso
    - Orquestração do processamento paralelo

- **Infrastructure**
    - Kafka Consumer / Producer
    - Persistência PostgreSQL
    - Configurações técnicas

---

##  Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3**
- **Spring Kafka**
- **Apache Kafka**
- **PostgreSQL**
- **Docker / Docker Compose**
- **CompletableFuture**
- **Micrometer + Actuator**
- **Logback (JSON logs)**
- **JUnit 5**
- **Mockito**
- **JaCoCo**

---

##  Regras de Validação

As validações são independentes e executadas **em paralelo**:

1. Valor entre **R$ 1.000 e R$ 5.000.000**
2. Moeda suportada: **BRL, USD, EUR**
3. Prazo entre **12 e 360 meses**
4. Data de início não pode estar no passado
5. Idade do cliente entre **18 e 75**
6. Tier de risco válido: **A, B, C, D**
7. Garantia obrigatória para valores acima de **R$ 1.000.000**
8. Renda anual mínima conforme o produto

Cada regra retorna:

- `contractId`
- `ruleName`
- `passed`
- `message`
- `durationMs`

---

##  Processamento Paralelo

As validações são executadas com `CompletableFuture`, permitindo:

- Melhor throughput
- Isolamento entre regras
- Facilidade de escalabilidade

A consolidação define o status geral:

- `ALL_PASSED` → todas as regras aprovadas
- `SOME_FAILED` → ao menos uma falhou

---

##  Persistência

- Banco: **PostgreSQL**
- Persistência via **batch insert**
- Estrutura da tabela criada automaticamente via script SQL

 Script:
docker/postgres/init/001_create_validation_results.sql

yaml
Copiar código

---

##  Testes

### Testes Unitários
- Regras de validação
- Caso de uso principal

### Cobertura
- Gerada com **JaCoCo**
- Foco em **Domain** e **Application**
- Infraestrutura excluída intencionalmente

### Executar testes e cobertura
```bash
mvn clean test jacoco:report
Relatório:

bash
Copiar código
target/site/jacoco/index.html
Observabilidade
Logs
Logs estruturados em JSON

Campos:

timestamp

level

correlationId

contractId

message

Configuração:

src/main/resources/logback-spring.xml
Actuator
Endpoints disponíveis:

Health:
GET /actuator/health

Readiness:
GET /actuator/health/readiness

Liveness:
GET /actuator/health/liveness

Métricas:
GET /actuator/metrics

Prometheus:
GET /actuator/prometheus


Subindo o ambiente (Docker)
Subir Kafka + PostgreSQL

docker compose up -d

Criar tópicos Kafka

docker exec -it kafka bash -lc "kafka-topics --bootstrap-server kafka:29092 --create --if-not-exists --topic contracts-input --partitions 3 --replication-factor 1"

docker exec -it kafka bash -lc "kafka-topics --bootstrap-server kafka:29092 --create --if-not-exists --topic contracts-validation-results --partitions 3 --replication-factor 1"

docker exec -it kafka bash -lc "kafka-topics --bootstrap-server kafka:29092 --create --if-not-exists --topic contracts-input-dlq --partitions 3 --replication-factor 1"

Executando a aplicação
mvn spring-boot:run

Enviando contrato (Producer)
docker exec -it kafka bash -lc "kafka-console-producer --bootstrap-server kafka:29092 --topic contracts-input"

Exemplo de payload válido:
{
  "contractId": "CNT-TEST-001",
  "clientId": "CL-123",
  "productType": "CREDIT",
  "amount": 250000,
  "currency": "BRL",
  "termMonths": 60,
  "startDate": "2026-02-15",
  "customerAge": 35,
  "clientRiskTier": "A",
  "collateralProvided": true,
  "attributes": {
    "annualIncome": 90000
  }
}

Consumindo resultados
docker exec -it kafka bash -lc "kafka-console-consumer --bootstrap-server kafka:29092 --topic contracts-validation-results --from-beginning"

Testando DLQ
docker exec -it kafka bash -lc "kafka-console-consumer --bootstrap-server kafka:29092 --topic contracts-input-dlq --from-beginning"

Envie um JSON inválido para o tópico de entrada:
{"contractId":

```
## Considerações Finais

Este projeto foi desenvolvido com foco em:

- Clareza arquitetural
- Escalabilidade e paralelismo
- Boas práticas de mensageria
- Observabilidade e confiabilidade
- Código limpo, testável e extensível

O serviço está preparado para execução em ambientes distribuídos, suportando múltiplas instâncias via consumer groups Kafka e mantendo consistência dos dados persistidos.

Funcionalidades adicionais como tracing distribuído e dashboards de métricas podem ser facilmente adicionadas sem impacto estrutural.

---
Desenvolvido por **Larissa Cordeiro**  
Java Backend Developer
