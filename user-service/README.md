# User Service

Microservice for managing users and their payment cards (PostgreSQL, Redis cache, Liquibase).

## Requirements

- Java 21+
- Maven 3.9+
- Docker (for integration tests and compose)

## Environment variables

See [`.env.example`](.env.example). All database and Redis settings are externalized.

| Variable | Description |
|----------|-------------|
| `SPRING_PROFILES_ACTIVE` | `local` or `docker` |
| `DB_URL` | JDBC URL for PostgreSQL |
| `DB_USERNAME` | Application DB user (not `postgres` superuser) |
| `DB_PASSWORD` | Application DB password |
| `POSTGRES_*` / `APP_DB_*` | Used by Docker Compose and `docker/postgres/init` |
| `REDIS_HOST` | Redis host |
| `REDIS_PORT` | Redis port |
| `SERVER_PORT` | HTTP port (default 8080) |

## Run locally

1. Copy `.env.example` to `.env` in this module and set values.
2. Start PostgreSQL and Redis (or use Docker Compose for infra only).
3. Create the DB and application user (same grants as `docker/postgres/init/01-create-app-user.sh`), or run Compose once to provision them.
4. From repository root:

```bash
cd user-service
mvn spring-boot:run
```

Spring loads variables from `user-service/.env` (file is gitignored).

## Run with Docker Compose

From `user-service/` (requires `.env` from `.env.example`):

```bash
docker compose up --build
```

Postgres starts as superuser `POSTGRES_USER` only for init. The init script creates `APP_DB_USERNAME` with DML (+ schema DDL for Liquibase) on `POSTGRES_DB`. The app connects as `APP_DB_USERNAME`, not as `postgres`.

## API (v1)

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/v1/users` | Create user |
| GET | `/api/v1/users/{id}` | Get user with cards (cached) |
| GET | `/api/v1/users?name&surname&page&size` | List users (filtered, paginated) |
| PUT | `/api/v1/users/{id}` | Update user |
| PATCH | `/api/v1/users/{id}/activate` | Activate user |
| PATCH | `/api/v1/users/{id}/deactivate` | Deactivate user and cards |
| DELETE | `/api/v1/users/{id}` | Delete user |
| POST | `/api/v1/users/{userId}/cards` | Create card (max 5 per user) |
| GET | `/api/v1/users/{userId}/cards` | List cards by user |
| GET | `/api/v1/cards` | List all cards (paginated) |
| GET | `/api/v1/cards/{id}` | Get card by id |
| PUT | `/api/v1/cards/{id}` | Update card |
| PATCH | `/api/v1/cards/{id}/activate` | Activate card |
| PATCH | `/api/v1/cards/{id}/deactivate` | Deactivate card |
| DELETE | `/api/v1/cards/{id}` | Delete card |

## Tests

```bash
mvn -pl user-service test
mvn -pl user-service verify
```

Integration tests use Testcontainers (PostgreSQL + Redis).
