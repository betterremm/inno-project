# Auth Service

Issues JWT access/refresh tokens and stores login credentials in a dedicated PostgreSQL database.

## Endpoints


| Method | Path                                         | Description                            |
| ------ | -------------------------------------------- | -------------------------------------- |
| POST   | `/api/v1/auth/credentials`                   | Save user login/password (BCrypt hash) |
| POST   | `/api/v1/auth/login` or `/api/v1/auth/token` | Login, returns access + refresh JWT    |
| POST   | `/api/v1/auth/validate`                      | Validate access token                  |
| POST   | `/api/v1/auth/refresh`                       | Refresh token pair                     |


JWT claims: `userId`, `role` (`ADMIN` / `USER`), `tokenType` (`ACCESS` / `REFRESH`).

## Setup

```bash
cp .env.example .env
docker compose up --build
```

Use the same `JWT_SECRET` in `user-service/.env`.

## Example flow

1. Create user in **user-service** (`POST /api/v1/users`) as admin.
2. Register credentials: `POST /api/v1/auth/credentials` with `userId`, `login`, `password`, `role`.
3. Login: `POST /api/v1/auth/login` → use `accessToken` as `Authorization: Bearer ...` for user-service.

