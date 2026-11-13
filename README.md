# budgeting-app

Personal budgeting app based on proportional allocation (50/30/20 rule: Needs, Wants, Savings).

## Overview

A full-stack personal finance application designed to import, categorize, and track expenses against proportional budget targets.

**Implemented**

- **Bank Ingestion:** Extensible ingestion pipeline using the Strategy pattern to parse and deduplicate Belgian bank statements (currently Belfius CSV exports only).
- **Categories & Buckets:** Categories are assigned to one of three budget buckets (**Needs**, **Wants**, **Savings**), and transactions can be assigned to a category.

**Planned**

- **Macro Buckets:** Balance incoming cash flow across **Needs** (50%), **Wants** (30%), and **Savings** (20%) with customizable allocation targets.
- **Proportional Tracking:** Calculate actual vs. target spending dynamically based on monthly net income, treating savings and investments as distinct goals rather than standard expenses.

## Tech Stack & Architecture

- **Backend:** Java 21, Spring Boot 4, Spring Data JPA, PostgreSQL, Maven.
- **Frontend:** Angular 22 (standalone components, TypeScript), Angular Material, Vitest.
- **Database:** PostgreSQL 16 containerized via Docker.
- **Design Patterns & Principles:**
  - Layered REST architecture (`Controller` &rarr; `Service` &rarr; `Repository` &rarr; `Entity`).
  - Strict decoupling of persistence models and API payloads via dedicated Request/Response DTOs.
  - Strategy Pattern for multi-bank CSV statement parsing.

## Layout

```text
backend/              Spring Boot API (Java 21, JPA, PostgreSQL)
frontend/             Angular SPA (TypeScript, standalone components)
docker-compose.yml    Local Postgres container configuration
.env.example          Template for local secrets (safe to commit)
.env                  Your local secrets (gitignored — never commit)
```

## Prerequisites

- Java 21 (`java -version`)
- Node 22.22.3+ or 24.15.0+ (`node -v`), as required by Angular 22
- Docker Desktop

## Quick start

Run each service in its own terminal, starting from the repository root.

```bash
# 0. Local secrets (once)
cp .env.example .env

# 1. Database
docker compose up -d

# 2. Backend (http://localhost:8080)
cd backend && ./mvnw spring-boot:run

# 3. Frontend (http://localhost:4200) — proxies /api to the backend
cd frontend && npm install && npm start
```

## Tests

```bash
cd backend && ./mvnw test    # BackendApplicationTests needs the database running
cd frontend && npm test      # Vitest
```
