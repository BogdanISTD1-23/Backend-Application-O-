-- =============================================
-- О! Mobile Operator — Database Schema
-- =============================================

-- Roles
CREATE TABLE IF NOT EXISTS roles (
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(50) NOT NULL UNIQUE
);

-- App Users (for Spring Security)
CREATE TABLE IF NOT EXISTS app_users (
    id          BIGSERIAL PRIMARY KEY,
    username    VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    enabled     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP DEFAULT NOW()
);

-- User ↔ Role mapping
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id)     ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Tariffs
CREATE TABLE IF NOT EXISTS tariffs (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    price       NUMERIC(10,2) NOT NULL,
    speed_mbps  INTEGER,
    active      BOOLEAN NOT NULL DEFAULT TRUE
);

-- Subscribers
CREATE TABLE IF NOT EXISTS subscribers (
    id              BIGSERIAL PRIMARY KEY,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    msisdn          VARCHAR(20)  NOT NULL UNIQUE,
    email           VARCHAR(150),
    balance         NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    tariff_id       BIGINT REFERENCES tariffs(id),
    photo_path      VARCHAR(500),
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    registered_at   TIMESTAMP DEFAULT NOW()
);

-- Transactions (top-up / payment)
CREATE TABLE IF NOT EXISTS transactions (
    id              BIGSERIAL PRIMARY KEY,
    subscriber_id   BIGINT NOT NULL REFERENCES subscribers(id) ON DELETE CASCADE,
    amount          NUMERIC(12,2) NOT NULL,
    type            VARCHAR(30) NOT NULL,
    description     VARCHAR(255),
    created_at      TIMESTAMP DEFAULT NOW()
);

-- =============================================
-- Seed Data
-- =============================================

INSERT INTO roles (name) VALUES ('ROLE_ADMIN'), ('ROLE_SUBSCRIBER')
ON CONFLICT (name) DO NOTHING;

-- Admin user is created/updated on startup by the application (see DataInitializer).

-- Tariff seed data
INSERT INTO tariffs (name, description, price, speed_mbps) VALUES
    ('Стандарт',   'Базовый тариф для звонков и интернета', 199.00, 10),
    ('Оптимум',    'Оптимальный тариф с 5 ГБ интернета',   399.00, 30),
    ('Максимум',   'Безлимитный интернет + звонки',        699.00, 100),
    ('Бизнес',     'Корпоративный тариф',                  1299.00, 300)
ON CONFLICT (name) DO NOTHING;
