# budgeting-app

Personal zero-based budgeting app.

## Layout

```text
backend/   Spring Boot 4 API (Java 21, JPA, PostgreSQL)
frontend/  Angular SPA (TypeScript, standalone components)
```

Local Postgres will live in a root `docker-compose.yml` (next step).

## Prerequisites

- Java 21 (`java -version`)
- Node 22+ (`node -v`)
- Docker Desktop (for the database, later)

## Quick start (after scaffolding)

```bash
# Backend
cd backend && ./mvnw spring-boot:run

# Frontend
cd frontend && npm start
```
