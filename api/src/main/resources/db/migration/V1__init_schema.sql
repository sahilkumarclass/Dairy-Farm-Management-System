-- Dairy Farm Management System — initial schema (Phase 1 MVP)

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================
-- users
-- ============================================================
CREATE TABLE users (
    id             UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    username       VARCHAR(80)  NOT NULL UNIQUE,
    password_hash  VARCHAR(255) NOT NULL,
    full_name      VARCHAR(120) NOT NULL,
    phone          VARCHAR(20),
    role           VARCHAR(20)  NOT NULL CHECK (role IN ('OWNER','STAFF','CUSTOMER')),
    enabled        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    version        BIGINT       NOT NULL DEFAULT 0
);
CREATE INDEX idx_users_role ON users(role);

-- ============================================================
-- customers
-- ============================================================
CREATE TABLE customers (
    id                 UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id            UUID         REFERENCES users(id) ON DELETE SET NULL,
    name               VARCHAR(120) NOT NULL,
    phone              VARCHAR(20)  NOT NULL,
    address            VARCHAR(255),
    custom_milk_rate   NUMERIC(10,2),
    status             VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','INACTIVE')),
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    version            BIGINT       NOT NULL DEFAULT 0
);
CREATE INDEX idx_customers_status ON customers(status);
CREATE INDEX idx_customers_phone  ON customers(phone);

-- ============================================================
-- milk_entries
-- ============================================================
CREATE TABLE milk_entries (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id     UUID         NOT NULL REFERENCES customers(id) ON DELETE RESTRICT,
    milk_type       VARCHAR(20)  NOT NULL CHECK (milk_type IN ('COW','BUFFALO','MIXED')),
    quantity_liters NUMERIC(10,3) NOT NULL CHECK (quantity_liters > 0),
    rate_per_liter  NUMERIC(10,2) NOT NULL CHECK (rate_per_liter >= 0),
    total_amount    NUMERIC(12,2) NOT NULL,
    session         VARCHAR(10)  NOT NULL CHECK (session IN ('MORNING','EVENING')),
    entry_date      DATE         NOT NULL,
    notes           VARCHAR(255),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    version         BIGINT       NOT NULL DEFAULT 0
);
CREATE INDEX idx_milk_entries_customer_date ON milk_entries(customer_id, entry_date);
CREATE INDEX idx_milk_entries_date          ON milk_entries(entry_date);

-- ============================================================
-- expenses
-- ============================================================
CREATE TABLE expenses (
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    category      VARCHAR(40)  NOT NULL CHECK (category IN ('FEED','VETERINARY','LABOR','UTILITIES','EQUIPMENT','TRANSPORT','OTHER')),
    amount        NUMERIC(12,2) NOT NULL CHECK (amount >= 0),
    notes         VARCHAR(500),
    expense_date  DATE         NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    version       BIGINT       NOT NULL DEFAULT 0
);
CREATE INDEX idx_expenses_date     ON expenses(expense_date);
CREATE INDEX idx_expenses_category ON expenses(category);

-- ============================================================
-- bills
-- ============================================================
CREATE TABLE bills (
    id                UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id       UUID         NOT NULL REFERENCES customers(id) ON DELETE RESTRICT,
    period_month      INT          NOT NULL CHECK (period_month BETWEEN 1 AND 12),
    period_year       INT          NOT NULL CHECK (period_year BETWEEN 2000 AND 2100),
    total_liters      NUMERIC(12,3) NOT NULL DEFAULT 0,
    total_amount      NUMERIC(12,2) NOT NULL DEFAULT 0,
    paid_amount       NUMERIC(12,2) NOT NULL DEFAULT 0,
    remaining_amount  NUMERIC(12,2) NOT NULL DEFAULT 0,
    status            VARCHAR(20)  NOT NULL DEFAULT 'UNPAID' CHECK (status IN ('UNPAID','PARTIAL','PAID')),
    generated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    version           BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT uq_bill_per_customer_period UNIQUE (customer_id, period_year, period_month)
);
CREATE INDEX idx_bills_customer ON bills(customer_id);
CREATE INDEX idx_bills_status   ON bills(status);

-- ============================================================
-- payments
-- ============================================================
CREATE TABLE payments (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    bill_id         UUID         NOT NULL REFERENCES bills(id) ON DELETE CASCADE,
    amount          NUMERIC(12,2) NOT NULL CHECK (amount > 0),
    payment_method  VARCHAR(20)  NOT NULL CHECK (payment_method IN ('CASH','UPI','BANK_TRANSFER','CARD')),
    payment_date    DATE         NOT NULL,
    reference       VARCHAR(120),
    notes           VARCHAR(255),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    version         BIGINT       NOT NULL DEFAULT 0
);
CREATE INDEX idx_payments_bill ON payments(bill_id);
CREATE INDEX idx_payments_date ON payments(payment_date);

-- Bootstrap owner is seeded by AdminBootstrap on first startup with a real BCrypt hash
-- of the password from APP_ADMIN_PASSWORD (default: admin123). Change immediately in production.
