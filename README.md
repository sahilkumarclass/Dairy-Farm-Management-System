# Dairy Farm Management System (DFMS)

Phase 1 MVP: Spring Boot 3 + React 18 (TypeScript) + PostgreSQL.

## Modules

| Module | Status |
|---|---|
| Auth (JWT, OWNER/STAFF/CUSTOMER roles) | ✅ |
| Customers (CRUD, search, custom rates, portal logins) | ✅ |
| Milk Entries (auto-total from rate × liters) | ✅ |
| Expenses (categorized, optional per-cow tagging) | ✅ |
| Billing (monthly generation, payments, status) | ✅ |
| Dashboard (today / month KPIs) | ✅ |
| Herd Register — cow CRUD, per-cow milk production, vet/vaccination log with cost | ✅ |
| Customer Portal — **separate React app** (`customer-web/`) for CUSTOMER role: own dashboard, milk history, bills | ✅ |

Still pending from the architecture doc: WhatsApp/SMS notifications, file uploads, milk-stock aggregation, multi-farm support.

## Customer portal access

The customer portal is a **separate frontend app** at `customer-web/` (port `5174`). It only accepts the `CUSTOMER` role; OWNER/STAFF accounts are bounced with a toast. The admin app (`web/`, port `5173`) does the inverse — customers are rejected and told to use the portal URL. Both apps share the same Spring Boot API.

To give a customer access:

1. As an OWNER, open **Customers** in the admin app.
2. Click **Create login** on a row, set a username/password.
3. Share the portal URL (`http://localhost:5174` in dev) — the customer signs in there and lands on their dashboard.

Behind the scenes this hits `POST /api/customers/{id}/login`, which creates a `User` with role `CUSTOMER` and links it 1:1 to the existing `Customer` row (unique index `uq_customers_user`). The portal calls `/api/me/*` endpoints (`CustomerSelfController`) that are role-scoped to the signed-in customer.

## Herd register

- `/herd` — list of cows with health-status filter and per-month summary (total cows, healthy, under treatment, dry, month production, vet cost, per-cow expenses).
- `/herd/:id` — cow detail: month/lifetime yield, vet cost, total tagged expenses, plus per-session production log and health-event log.
- Daily milk production per cow is logged separately from per-customer milk entries — they're different concerns (one tracks what each cow produced, the other tracks what each customer took home).
- Expenses can be tagged to a cow (e.g. feed bought for a specific cow, vet fee for a sick cow) — the cow detail screen rolls these up.

## Repo layout

```
api/           Spring Boot backend (Java 21, Maven)
web/           Admin app — React + Vite + TypeScript (port 5173)
customer-web/  Customer portal app — React + Vite + TypeScript (port 5174)
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

# 3. Admin app (port 5173) — in another shell
cd web
npm install
npm run dev

# 4. Customer portal (port 5174) — in another shell
cd customer-web
npm install
npm run dev
```

- Admin app at http://localhost:5173 — sign in with **admin / admin123** (seeded in `V1__init_schema.sql`).
- Customer portal at http://localhost:5174 — sign in with a customer login created from the admin app's Customers screen.

API docs: http://localhost:8080/swagger-ui.html

## Configuration

Backend config comes from `api/src/main/resources/application.yml` and env vars (see `.env.example`):

| Var | Default | Purpose |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/dfms` | Postgres JDBC URL |
| `DB_USER` / `DB_PASSWORD` | `dfms` / `dfms` | DB credentials |
| `JWT_SECRET` | (placeholder) | **Replace** in production. Min 32 bytes for HS256. |
| `CORS_ORIGINS` | `http://localhost:5173,http://localhost:5174` | Comma-separated allowed origins (admin + customer apps) |

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
- File uploads (invoice/expense photos), WhatsApp/SMS notifications, aggregate milk-stock tracker, multi-farm support: Phases 2–3.
