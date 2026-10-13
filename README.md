# Por-Onde-Alagoas-API

Backend Spring Boot para planejamento de roteiros turísticos em Alagoas.

## Tecnologias

- Java 17
- Spring Boot 3.3.5
- Spring Web
- Spring Data JPA
- Bean Validation
- Spring Boot Actuator
- PostgreSQL
- Flyway
- JUnit 5 e Mockito

## Variáveis de ambiente

Copie `.env.example` e configure:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `APP_CORS_ALLOWED_ORIGINS`
- `SPRING_PROFILES_ACTIVE`

## Perfis

- `dev` (default)
- `prod`

## Executar

```bash
mvn spring-boot:run
```

## Endpoints

### Destinos

- `GET /api/destinos`
- `GET /api/destinos/{id}`
- `GET /api/destinos?municipio=Maceió`
- `GET /api/destinos?categoria=PRAIA`
- Filtros podem ser combinados e aceitam paginação (`page`, `size`, `sort`)

### Roteiros

- `POST /api/roteiros/gerar`
- `GET /api/roteiros/{id}`

Exemplo de payload para geração:

```json
{
  "quantidadeDias": 3,
  "orcamentoDisponivel": 450.00,
  "interesses": ["PRAIA", "CULTURA"],
  "municipiosInteresse": ["Maceió", "Maragogi"]
}
```

## Regras de geração (MVP)

- Seleciona destinos publicados por categoria/interesse e município opcional.
- Não repete destinos no mesmo roteiro.
- Distribui itens entre dias considerando duração estimada quando disponível.
- Usa orçamento como estimativa de custos conhecidos (`custoEstimado` não nulo).
- Destinos sem custo conhecido são incluídos, mas separados na explicação.
- Retorna explicação com limitações quando não há dados suficientes.

## Health check

- `GET /actuator/health`
