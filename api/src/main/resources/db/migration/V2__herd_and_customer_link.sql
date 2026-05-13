-- V2 — Herd management (cows, per-cow milk production, health logs) +
--      optional cow link on expenses + ensure customer<->user link is unique.

-- ============================================================
-- cows
-- ============================================================
CREATE TABLE cows (
    id               UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tag_no           VARCHAR(40)  NOT NULL UNIQUE,
    name             VARCHAR(80),
    breed            VARCHAR(60),
    gender           VARCHAR(10)  NOT NULL DEFAULT 'FEMALE' CHECK (gender IN ('FEMALE','MALE')),
    age_months       INT          CHECK (age_months IS NULL OR age_months >= 0),
    health_status    VARCHAR(20)  NOT NULL DEFAULT 'HEALTHY' CHECK (health_status IN ('HEALTHY','UNDER_TREATMENT','DRY','SOLD','DECEASED')),
    daily_yield_estimate_liters NUMERIC(10,3),
    date_acquired    DATE,
    notes            VARCHAR(500),
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    version          BIGINT       NOT NULL DEFAULT 0
);
CREATE INDEX idx_cows_health ON cows(health_status);
CREATE INDEX idx_cows_breed  ON cows(breed);

-- ============================================================
-- cow_milk_production
-- per-cow milk yield, per session
-- ============================================================
CREATE TABLE cow_milk_production (
    id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    cow_id          UUID          NOT NULL REFERENCES cows(id) ON DELETE CASCADE,
    production_date DATE          NOT NULL,
    session         VARCHAR(10)   NOT NULL CHECK (session IN ('MORNING','EVENING')),
    liters          NUMERIC(10,3) NOT NULL CHECK (liters >= 0),
    notes           VARCHAR(255),
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    version         BIGINT        NOT NULL DEFAULT 0,
    CONSTRAINT uq_cow_production UNIQUE (cow_id, production_date, session)
);
CREATE INDEX idx_cow_prod_date ON cow_milk_production(production_date);
CREATE INDEX idx_cow_prod_cow  ON cow_milk_production(cow_id);

-- ============================================================
-- cow_health_logs
-- vet visits, vaccinations, treatments — all with optional cost
-- ============================================================
CREATE TABLE cow_health_logs (
    id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    cow_id       UUID         NOT NULL REFERENCES cows(id) ON DELETE CASCADE,
    event_type   VARCHAR(30)  NOT NULL CHECK (event_type IN ('VACCINATION','VET_VISIT','TREATMENT','CHECKUP','OTHER')),
    event_date   DATE         NOT NULL,
    next_due_date DATE,
    vet_name     VARCHAR(120),
    cost         NUMERIC(12,2) CHECK (cost IS NULL OR cost >= 0),
    notes        VARCHAR(500),
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    version      BIGINT       NOT NULL DEFAULT 0
);
CREATE INDEX idx_health_cow   ON cow_health_logs(cow_id);
CREATE INDEX idx_health_date  ON cow_health_logs(event_date);
CREATE INDEX idx_health_due   ON cow_health_logs(next_due_date);

-- ============================================================
-- expenses: optional link to a cow (feed, vet, etc.)
-- ============================================================
ALTER TABLE expenses
    ADD COLUMN cow_id UUID NULL REFERENCES cows(id) ON DELETE SET NULL;
CREATE INDEX idx_expenses_cow ON expenses(cow_id);

-- ============================================================
-- customers <-> users: enforce 1:1 so a customer can self-login
-- ============================================================
CREATE UNIQUE INDEX uq_customers_user ON customers(user_id) WHERE user_id IS NOT NULL;
