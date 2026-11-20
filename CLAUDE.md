# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Personal budgeting app based on proportional allocation (50/30/20: Needs / Wants / Savings). Bank statement CSVs (currently Belfius only) are imported, deduplicated, and categorized; each `Category` belongs to a `BudgetBucket` (`NEEDS`, `WANTS`, `SAVINGS`). The 50/30/20 allocation targets and actual-vs-target tracking described in the README are **planned, not yet implemented** — there is no income or allocation logic in the code yet.

Monorepo: `backend/` (Spring Boot API), `frontend/` (Angular SPA), `docker-compose.yml` (Postgres 16).

## Commands

```bash
# One-time: local secrets (root .env is read by both docker compose and Spring)
cp .env.example .env

# Database
docker compose up -d

# Backend (http://localhost:8080) — run from backend/
./mvnw spring-boot:run
./mvnw test                                          # all tests
./mvnw test -Dtest=BelfiusCsvParserTests             # single class
./mvnw test -Dtest=TransactionServiceTests#methodName # single method

# Frontend (http://localhost:4200) — run from frontend/ (Node 22.22.3+ or 24.15.0+)
npm install
npm start            # ng serve, proxies /api -> localhost:8080 (proxy.conf.json)
npm run build
npm test             # ng test (Vitest via @angular/build:unit-test, jsdom)
npx prettier --write <files>   # formatting (printWidth 100, single quotes)
```

`BackendApplicationTests` (`@SpringBootTest`) needs the Postgres container running; controller tests (`@WebMvcTest` + `@MockitoBean`) and service/parser tests (Mockito) do not.

## Backend architecture

Java 21, **Spring Boot 4.x** (`pom.xml` uses 4.1.1). Consequences: Jackson 3 (`tools.jackson.databind.ObjectMapper`), `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`, `@MockitoBean` instead of `@MockBean`.

Package `com.daanberger.budgeting`, layered `controllers → services → repositories → domain`:
- **DTOs** live in `controllers/dto/` (mostly records). Entities are never returned from controllers; services map to `TransactionResponse` / `CategoryDTO`.
- **Errors**: services throw custom exceptions from `exceptions/`; `ApiExceptionHandler` (`@RestControllerAdvice`) maps them to RFC 7807 `ProblemDetail` responses (404/409/400; validation errors collected into an `errors` map). New exception types need a handler there.
- **Entities** use app-generated `UUID` ids assigned in the constructor, a protected no-arg JPA constructor, and no Lombok. Schema is managed by Hibernate `ddl-auto=update` (no migrations tool).
- **CSV ingestion** (`POST /api/transaction/import`, multipart field `file`): `TransactionIngestionService` calls a `BankCsvParser` (Strategy interface in `parsing/`) to get `RawTransactionDto`s, then skips rows where `existsByDateAndAmountAndDescription` is true (also enforced by a DB unique constraint on `transaction(date, amount, description)`). Imported transactions have no category; categories are assigned later via `PATCH /api/transaction/{id}/assign-category`.
  - `BelfiusCsvParser` skips preamble lines until the header starting with `Rekening;Boekingsdatum`, uses `;` delimiter, Dutch column names, `dd/MM/uuuu` dates, and comma decimals. Parse failures are wrapped in `BelfiusCsvParseException` → 400.
  - Only one `BankCsvParser` bean exists and it is injected directly; adding a second bank parser requires a selection mechanism (qualifier/map/registry) in the ingestion service.

Config: `application.properties` imports `../.env` (relative to `backend/`) for `POSTGRES_*` / `DB_HOST` / `DB_PORT`, so run the backend from the `backend/` directory.

## Frontend architecture

Angular 22, standalone components, Angular Material (theme in `src/material-theme.scss`), `provideHttpClient()` in `app.config.ts`. Code is organized by feature under `src/app/features/<feature>/` with `pages/`, `components/`, `services/`, `models/` subfolders. Pages are lazy-loaded via `loadComponent` in `app.routes.ts`. Component files follow the new naming convention (`transaction-list.ts`, class `TransactionList`, no `.component` suffix). Services use `inject()` and call relative `/api/...` URLs (relying on the dev proxy). Frontend models mirror backend response DTOs.
