# Contract Validation Service

##  Overview

Serviço responsável por validar contratos financeiros de forma assíncrona e paralela.
O sistema consome contratos via Apache Kafka, executa regras de validação independentes
em paralelo e publica o resultado consolidado em um tópico de saída.

Mensagens inválidas são direcionadas para uma Dead Letter Queue (DLQ),
garantindo resiliência e isolamento de falhas.

---

##  Arquitetura

O projeto segue uma arquitetura **Hexagonal (Ports & Adapters)**, promovendo
separação de responsabilidades e desacoplamento entre domínio e infraestrutura.

### Camadas

- **Domain**
    - Entidades de negócio (`Contract`, `ValidationResult`)
    - Regras de validação independentes
- **Application**
    - Casos de uso responsáveis pela orquestração das validações
    - Execução paralela com `CompletableFuture`
- **Infrastructure**
    - Integração com Apache Kafka (Consumer, Producer, DLQ)
    - Mapeamento de mensagens e configurações

Essa abordagem facilita testes, manutenção e evolução do sistema.

---

##  Fluxo de Processamento

1. O serviço consome contratos do tópico Kafka `contracts-input`
2. O payload JSON é desserializado para o modelo de domínio
3. As validações são executadas de forma paralela
4. Os resultados são consolidados em um status geral:
    - `ALL_PASSED`
    - `SOME_FAILED`
5. O resultado consolidado é publicado no tópico `contracts-validation-results`
6. Mensagens inválidas são redirecionadas para a DLQ `contracts-input-dlq`

---

##  Validações Implementadas

- Validação de valor do contrato
- Validação de moeda suportada
- Validação de prazo
- Validação de data de início
- Validação de idade do cliente
- Validação de tier de risco
- Validação de renda mínima
- Validação de colateral para contratos de alto valor

---

## ⚙ Tecnologias Utilizadas

- Java 17
- Spring Boot
- Spring Kafka
- Apache Kafka
- Docker e Docker Compose
- JUnit 5

---

##  Observabilidade

- Logs estruturados com `contractId` e `correlationId`
- Health checks e métricas via Spring Boot Actuator
- Preparado para integração com Prometheus

---

## ▶ Como Executar o Projeto

### Pré-requisitos

- Java 17+
- Maven
- Docker Desktop

---

### Subir o Kafka

```bash
docker compose up -d
Verifique se os containers estão ativos:

docker ps
- Criar os Tópicos Kafka
docker exec -it kafka bash -lc "kafka-topics --bootstrap-server kafka:29092 --create --if-not-exists --topic contracts-input --partitions 3 --replication-factor 1"
docker exec -it kafka bash -lc "kafka-topics --bootstrap-server kafka:29092 --create --if-not-exists --topic contracts-validation-results --partitions 3 --replication-factor 1"
docker exec -it kafka bash -lc "kafka-topics --bootstrap-server kafka:29092 --create --if-not-exists --topic contracts-input-dlq --partitions 3 --replication-factor 1"

- Rodar a Aplicação
mvn spring-boot:run
A aplicação ficará aguardando mensagens no tópico contracts-input.

- Enviar um Contrato para Validação
Abra um producer Kafka:

docker exec -it kafka bash -lc "kafka-console-producer --bootstrap-server kafka:29092 --topic contracts-input"
Exemplo de payload:

{
  "contractId": "CNT-2026-0001",
  "clientId": "CL-12345",
  "productType": "CREDIT",
  "amount": 250000.00,
  "currency": "BRL",
  "termMonths": 60,
  "startDate": "2026-01-30",
  "customerAge": 35,
  "clientRiskTier": "A",
  "collateralProvided": true,
  "attributes": {
    "annualIncome": 90000,
    "customerAge": 35,
    "riskTier": "LOW"
  }
}

- Consumir Resultados
docker exec -it kafka bash -lc "kafka-console-consumer --bootstrap-server kafka:29092 --topic contracts-validation-results --from-beginning"

- Testar Dead Letter Queue (DLQ)
Envie um payload inválido no tópico contracts-input e consuma a DLQ:

docker exec -it kafka bash -lc "kafka-console-consumer --bootstrap-server kafka:29092 --topic contracts-input-dlq --from-beginning"


Testes

Testes unitários para as regras de validação

Testes do caso de uso com execução paralela

Foco em regras de negócio e isolamento de responsabilidades

Considerações Finais
O projeto foi desenvolvido com foco em clareza arquitetural,
processamento eficiente e facilidade de evolução,
seguindo boas práticas de desenvolvimento e integração assíncrona.