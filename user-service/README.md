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
| `DB_USERNAME` | Database user |
| `DB_PASSWORD` | Database password |
| `REDIS_HOST` | Redis host |
| `REDIS_PORT` | Redis port |
| `SERVER_PORT` | HTTP port (default 8080) |

## Run locally

1. Start PostgreSQL and Redis (or use Docker only for infra).
2. Create database `user_service_db` and user `user_service`.
3. Run:

```bash
mvn -pl user-service spring-boot:run -Dspring-boot.run.profiles=local
```

## Run with Docker Compose

From repository root:

```bash
docker compose -f user-service/docker-compose.yml up --build
```

Uses profile `docker` with service hostnames `postgres` and `redis`.

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
