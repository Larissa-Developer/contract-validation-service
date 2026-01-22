# Contract Validation Service (Kafka + Parallel Validation + DLQ)

Serviço Java/Spring Boot para validação de contratos financeiros em alto volume com:
- Consumo de contratos via **Apache Kafka**
- Execução de **validações independentes em paralelo** (CompletableFuture)
- **Consolidação** do resultado (ALL_PASSED / SOME_FAILED)
- Publicação em tópico Kafka de saída
- Tratamento de mensagens inválidas via **DLQ**
- **Observabilidade** com Actuator/Micrometer (health, metrics, prometheus)
- **Testes** unitários e cobertura com JaCoCo (foco no core: Domain/Application)

---

##  Visão geral do fluxo

1. **Consumer** lê mensagens do tópico `contracts-input`
2. Payload JSON é validado/parseado para objeto de domínio
3. Regras de validação rodam **em paralelo**
4. Resultado consolidado é gerado com:
    - `timestamp`
    - `overallStatus` (`ALL_PASSED` / `SOME_FAILED`)
    - lista de resultados por regra (`ruleName`, `passed`, `message`, `durationMs`)
5. Resultado é publicado em `contracts-validation-results`
6. Mensagens inválidas ou que falhem no processamento são enviadas para `contracts-input-dlq`

---

##  Arquitetura (Hexagonal simplificada)

Estrutura em camadas com separação de responsabilidades:

- **Domain**
    - Modelos de domínio (ex.: `Contract`, `ValidationResult`)
    - Regras de validação (`ValidationRule`)
    - Portas (interfaces) para integrações (ex.: mensageria, persistência)
- **Application**
    - Casos de uso (ex.: `ValidateContractUseCase`)
    - Orquestração das validações paralelas e consolidação
- **Infrastructure**
    - Adapters Kafka (consumer/producer)
    - Configurações Spring/Kafka
    - (Persistência e repositórios quando aplicável)

> Objetivo: manter regras de negócio no **Domain/Application** e deixar integrações em **Infrastructure**.

---

##  Regras de validação implementadas

As validações são independentes e executadas em paralelo:

1. **AmountValidationRule**  
   Valor entre **R$ 1.000* e *R$ 5.000.000**
2. **CurrencyValidationRule**  
   Moedas suportadas: **BRL, USD, EUR**
3. **TermValidationRule**  
   Prazo entre **12 e 360 meses**
4. **StartDateValidationRule**  
   Data de início **não pode estar no passado**
5. **AgeValidationRule**  
   Idade entre **18 e 75**
6. **RiskTierValidationRule**  
   Tier válido: **A, B, C, D**
7. **CollateralValidationRule**  
   Garantia obrigatória se `amount > 1.000.000`
8. **IncomeValidationRule**  
   Renda anual mínima conforme o produto (ex.: `annualIncome` em `attributes`)

Cada regra retorna um `ValidationResult` contendo:
- `contractId`
- `ruleName`
- `passed`
- `message`
- `durationMs`

---

##  Testes e cobertura

O foco dos testes está na **lógica de negócio** (Domain e Application):
- Testes unitários para regras de validação
- Testes do caso de uso com execução paralela
- Relatório de cobertura com **JaCoCo**

### Rodar testes
```bash
mvn test
Gerar relatório de cobertura (JaCoCo)
mvn clean test jacoco:report


- O relatório HTML fica em:
target/site/jacoco/index.html

Observação: classes puramente de infraestrutura (configuracao/adapters) podem ter cobertura menor por dependerem de ambiente externo.

- Observabilidade (Actuator + Micrometer)

Endpoints (padrao localhost:8080):

Health:

GET /actuator/health

Probes:

GET /actuator/health/liveness

GET /actuator/health/readiness

Metrics:

GET /actuator/metrics

Prometheus:

GET /actuator/prometheus

- Subir infraestrutura com Docker (Kafka + PostgreSQL)
- Subir os containers

Na raiz do projeto (onde esta docker-compose.yml):

docker compose up -d
docker ps


- Verifique se kafka e postgres estão com status Up.

- Kafka - Criar tópicos
- Criar os tópicos (uma vez)
docker exec -it kafka bash -lc "kafka-topics --bootstrap-server kafka:29092 --create --if-not-exists --topic contracts-input --partitions 3 --replication-factor 1"

docker exec -it kafka bash -lc "kafka-topics --bootstrap-server kafka:29092 --create --if-not-exists --topic contracts-validation-results --partitions 3 --replication-factor 1"

docker exec -it kafka bash -lc "kafka-topics --bootstrap-server kafka:29092 --create --if-not-exists --topic contracts-input-dlq --partitions 3 --replication-factor 1"

(Opcional) Listar tópicos
docker exec -it kafka bash -lc "kafka-topics --bootstrap-server kafka:29092 --list"

- Rodar a aplicação
- Subir o serviço
mvn spring-boot:run


A aplicação ficará aguardando mensagens no tópico contracts-input.

- Enviar contrato para validação (producer)
- Abrir producer no tópico de entrada
docker exec -it kafka bash -lc "kafka-console-producer --bootstrap-server kafka:29092 --topic contracts-input"

Enviar payload válido (cole em uma linha e pressione Enter)
{"contractId":"CNT-2026-0001","clientId":"CL-12345","productType":"CREDIT","amount":250000.00,"currency":"BRL","termMonths":60,"startDate":"2026-02-15","customerAge":35,"clientRiskTier":"A","collateralProvided":true,"attributes":{"annualIncome":90000}}

- Consumir resultados (consumer)
- Ler o tópico de resultados
docker exec -it kafka bash -lc "kafka-console-consumer --bootstrap-server kafka:29092 --topic contracts-validation-results --from-beginning"


- Você deve ver um JSON contendo:

contractId

timestamp

overallStatus (ALL_PASSED ou SOME_FAILED)

lista results[]

-Testar Dead Letter Queue (DLQ)

A DLQ recebe mensagens inválidas (ex.: JSON quebrado, campos obrigatorios faltando, erro de parsing).

-Abrir consumer da DLQ
docker exec -it kafka bash -lc "kafka-console-consumer --bootstrap-server kafka:29092 --topic contracts-input-dlq --from-beginning"

Enviar payload inválido (no producer do topico contracts-input)

Cole e pressione Enter:

{"contractId":


-A mensagem inválida deve aparecer no consumer da DLQ.