# Alertaid Backend

A Spring Boot 3 + Maven backend that serves a static HTML/JS frontend (placed in `frontend/`). Security uses JWT. Persistence is via JPA (MySQL locally by default; Postgres when using Docker Compose). Prebuilt JAR is produced on `mvn package`.

## Quick start

- Java 17+ is required (runs fine with JDK 23).
- Use the Maven wrapper:

```
./mvnw spring-boot:run        # Linux/macOS
mvnw.cmd spring-boot:run      # Windows
```

By default it connects to MySQL at `jdbc:mysql://localhost:3306/alertaid_db` with username `root` and password `root` (see `src/main/resources/application.properties`).

Set a JWT secret (base64 encoded 32+ byte key) if you want to override the default:

```
export APP_JWT_SECRET=$(openssl rand -base64 48)   # bash
# Windows PowerShell
$env:APP_JWT_SECRET = "<base64_secret>"
```

Then open http://localhost:8080 (static frontend is bundled into `static/` at build time).

## Endpoints (selected)

- Auth
  - POST `/api/auth/register` { name, email, password, phone, role }
  - POST `/api/auth/login` { email, password } -> { token, email, role }
  - POST `/api/auth/reset-password?email=...` -> stub OK
- Campaigns
  - GET `/api/campaigns`
  - POST `/api/campaigns`
- Donations
  - GET `/api/donations`
  - POST `/api/donations/{campaignId}` { donorName, donorEmail, amount }
- SeekForHelp
  - GET `/api/seekforhelp`
  - POST `/api/seekforhelp`
- Weather Alerts
  - GET `/api/weather-alerts`
  - POST `/api/weather-alerts`

Authorization: set header `Authorization: Bearer <jwt>` for protected POST/PUT/DELETE.

## Database schema

You can either let JPA create/update tables (default), or run SQL yourself.

- JPA auto DDL: `spring.jpa.hibernate.ddl-auto=update`
- SQL scripts (manual):
  - MySQL: `src/main/resources/db/sql/mysql_schema.sql`
  - Postgres: `src/main/resources/db/sql/postgres_schema.sql`

Note: Entities are kept minimal for the MVP and may not use all columns shown in the SQL design (e.g., payment_status). That’s intentional to keep the first release simple.

## Frontend integration

The static HTML pages already call backend APIs via `fetch`:
- login.html -> `/api/auth/login`, `/api/auth/register`
- campaigns.html -> `/api/campaigns`
- donation.html and donatemoney.html -> `/api/donations/{campaignId}` (Authorization required)
- alerts.html -> `/api/weather-alerts`
- report.html -> `/api/seekforhelp`

Tokens are stored in `localStorage.token` and attached as `Authorization: Bearer <token>` when present.

## Docker (PostgreSQL)

- Requirements: Docker Desktop
- Compose file at repo root uses Postgres and builds the backend image:

```
docker compose up --build
# Backend: http://localhost:8080
# DB:      localhost:5432 (service name: db)
```

Environment used in compose:
- `SPRING_PROFILES_ACTIVE=prod`
- `SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/alertaid`
- `SPRING_DATASOURCE_USERNAME=alertaid`
- `SPRING_DATASOURCE_PASSWORD=alertaid`
- `APP_JWT_SECRET=<base64 secret>`

## Cloud deployment (options)

1. Render / Railway / Fly.io (recommended for speed)
   - Build the Docker image using the provided `backend/Alertaid/Dockerfile`.
   - Provision a managed Postgres instance.
   - Set env vars: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `APP_JWT_SECRET`, `SPRING_PROFILES_ACTIVE=prod`.

2. AWS (ECS/Fargate or EC2)
   - Push the Docker image to ECR.
   - Run task/service with the same env vars and attach an RDS Postgres or MySQL.

## Testing

Run unit and MVC tests:
```
./mvnw -q -B test
```
Tests cover: AuthController (login OK path), CampaignService, VolunteerService, SeekForHelpService. H2 in-memory DB is used for tests (`src/test/resources/application.properties`).

## Notes
- CORS is enabled for localhost origins (CorsConfig) and the app serves the frontend from the `static/` directory.
- JWT default secret is for development only. Override with `APP_JWT_SECRET` in any non-dev environment.
