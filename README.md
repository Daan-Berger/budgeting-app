# budgeting-app

Personal zero-based budgeting app.

## Layout

```text
backend/              Spring Boot 4 API (Java 21, JPA, PostgreSQL)
frontend/             Angular SPA (TypeScript, standalone components)
docker-compose.yml    Local Postgres only
.env.example          Template for local secrets (safe to commit)
.env                  Your local secrets (gitignored — never commit)
```

## Prerequisites

- Java 21 (`java -version`)
- Node 22+ (`node -v`)
- Docker Desktop

## Quick start

```bash
# 0. Local secrets (once)
cp .env.example .env

# 1. Database
docker compose up -d

# 2. Backend (http://localhost:8080)
cd backend && ./mvnw spring-boot:run

# 3. Frontend (http://localhost:4200)
cd frontend && npm start
```
