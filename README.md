# Inno Project

Microservices course monorepo (Spring Boot 3.4, Java 21).

## Modules

| Module | Description |
|--------|-------------|
| [user-service](user-service/) | Users and payment cards (PostgreSQL, Redis, Liquibase) |

## Branch workflow

- `dev` — integration branch
- `feature/*` — per-service implementation, merged via pull request

## Build

```bash
mvn clean verify
```

Build a single module:

```bash
mvn -pl user-service -am clean package
```
