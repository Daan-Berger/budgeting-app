# budgeting-app

Personal budgeting app based on proportional allocation (50/30/20 rule: Needs, Wants, Savings).

## Overview

A full-stack personal finance application designed to import, categorize, and track expenses against proportional budget targets. 

- **Macro Buckets:** Automatically balances incoming cash flow across **Needs** (50%), **Wants** (30%), and **Savings** (20%) with customizable allocation targets.
- **Automated Bank Ingestion:** Extensible ingestion pipeline using the Strategy pattern to parse and deduplicate Belgian bank statements (starting with Belfius CSV exports).
- **Proportional Tracking:** Calculates actual vs. target spending dynamically based on monthly net income, treating savings and investments as distinct goals rather than standard expenses.

## Tech Stack & Architecture

- **Backend:** Java 21, Spring Boot 3, Spring Data JPA, PostgreSQL, Maven.
- **Frontend:** Angular (standalone components, TypeScript).
- **Database:** PostgreSQL containerized via Docker.
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
.env                  Your local secrets (gitignored — never commit)               Your local secrets (gitignored — never commit)
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
