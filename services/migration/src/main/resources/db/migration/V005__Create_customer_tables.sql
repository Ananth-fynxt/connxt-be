-- Migration: V005__Create_customer_tables.sql
-- Description: Create customer and wallet tables

-- Create brand_customer table
CREATE TABLE brand_customer (
    id TEXT PRIMARY KEY NOT NULL,
    brand_id TEXT NOT NULL REFERENCES brands(id),
    environment_id TEXT NOT NULL REFERENCES environments(id),
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    tag TEXT,
    account_type TEXT,
    country TEXT NOT NULL,
    currencies TEXT[] NOT NULL,
    customer_meta JSONB NOT NULL DEFAULT '{}',
    scope scope NOT NULL DEFAULT 'EXTERNAL',
    status user_status NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL
);

-- Create wallet table
CREATE TABLE wallet (
    id TEXT PRIMARY KEY NOT NULL,
    brand_id TEXT NOT NULL REFERENCES brands(id),
    environment_id TEXT NOT NULL REFERENCES environments(id),
    brand_customer_id TEXT NOT NULL REFERENCES brand_customer(id),
    name TEXT,
    currency TEXT NOT NULL,
    balance DECIMAL(20,8) NOT NULL DEFAULT 0.0,
    available_balance DECIMAL(20,8) NOT NULL DEFAULT 0.0,
    hold_balance DECIMAL(20,8) NOT NULL DEFAULT 0.0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL
);

-- Create indexes for better query performance
CREATE INDEX idx_brand_customer_brand_id ON brand_customer(brand_id, environment_id);
CREATE UNIQUE INDEX idx_brand_customer_id_brand_email ON brand_customer(id, brand_id, environment_id, email);

CREATE UNIQUE INDEX idx_wallet_brand_customer_currency ON wallet(brand_id, environment_id, brand_customer_id, currency);
CREATE INDEX idx_wallet_brand_customer ON wallet(brand_id, environment_id, brand_customer_id);
