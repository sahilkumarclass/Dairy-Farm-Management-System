# Dairy Farm Management System (DFMS)

Phase 1 MVP: Spring Boot 3 + React 18 (TypeScript) + PostgreSQL.

## Modules

| Module | Status |
|---|---|
| Auth (JWT, OWNER/STAFF/CUSTOMER roles) | ✅ |
| Customers (CRUD, search, custom rates) | ✅ |
| Milk Entries (auto-total from rate × liters) | ✅ |
| Expenses (categorized) | ✅ |
| Billing (monthly generation, payments, status) | ✅ |
| Dashboard (today / month KPIs) | ✅ |

Phases 2–3 from the architecture doc (notifications, stock, herd register, WhatsApp, etc.) are out of scope for this pass.

## Repo layout

```
api/   Spring Boot backend (Java 21, Maven)
web/   React + Vite + TypeScript frontend
docker-compose.yml   Postgres
```

## Prerequisites

- JDK 21
- Node 20+
- Docker (for Postgres) — or your own Postgres instance

## Run

```powershell
# 1. Start Postgres
docker compose up -d

# 2. Backend (port 8080)
cd api
./mvnw spring-boot:run

# 3. Frontend (port 5173) — in another shell
cd web
npm install
npm run dev
```

Open http://localhost:5173 and sign in with **admin / admin123** (seeded in `V1__init_schema.sql`).

API docs: http://localhost:8080/swagger-ui.html

## Configuration

Backend config comes from `api/src/main/resources/application.yml` and env vars (see `.env.example`):

| Var | Default | Purpose |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/dfms` | Postgres JDBC URL |
| `DB_USER` / `DB_PASSWORD` | `dfms` / `dfms` | DB credentials |
| `JWT_SECRET` | (placeholder) | **Replace** in production. Min 32 bytes for HS256. |
| `CORS_ORIGINS` | `http://localhost:5173` | Comma-separated allowed origins |

Frontend reads `VITE_API_BASE_URL` (defaults to `http://localhost:8080`).

## Architecture notes

- **Schema** is owned by Flyway (`api/src/main/resources/db/migration`). JPA runs with `ddl-auto: validate` — never `update`.
- **Each backend module** follows controller → service → repository, with DTOs at the boundary. Entities stay inside the module.
- **Errors** are normalized through `GlobalExceptionHandler` into a single `ApiError` shape so the frontend always knows where to read `.message` / `.violations`.
- **Auth** is stateless JWT (no sessions). `JwtAuthenticationFilter` reads the bearer token, loads the user via `UserDetailsService`, and populates the security context. Method-level `@PreAuthorize` enforces role guards.
- **Billing** generation is idempotent: re-running for the same period refreshes totals but preserves payments. A scheduled job runs at 03:00 on the 1st of every month for the previous month (`BillingService.scheduledMonthlyGeneration`).
- **Frontend** uses React Query for server state, Zustand (persisted) for auth, react-router for routing, and shadcn-style primitives over Radix. Currency formatted as INR.

## Default admin

`username: admin` / `password: admin123` — seeded via Flyway. **Rotate immediately** by registering a new OWNER (`POST /api/auth/register`) and disabling the seed in production.

## Known gaps

- No refresh-token flow yet — access tokens expire after 1h and the user re-logs in. Phase 2.
- No tests beyond the auto-generated `contextLoads`. That test currently requires Postgres to be up; consider adding `@DataJpaTest` with Testcontainers in Phase 2.
- Customer panel (architecture §2B) not yet built — backend supports the role, frontend currently routes only owners/staff into the shell.
- File uploads, notifications, herd register, stock tracking: Phases 2–3.
