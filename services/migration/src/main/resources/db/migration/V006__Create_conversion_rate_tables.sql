-- Migration: V006__Create_conversion_rate_tables.sql
-- Description: Create conversion rate tables (conversion_rate_setup, conversion_rate) with versioning
-- Service: shared

-- Create conversion_rate_currency_pairs table
CREATE TABLE fixer_api_currency_pairs (
    id TEXT PRIMARY KEY NOT NULL,
    source_currency TEXT UNIQUE NOT NULL,
    target_currency TEXT[] NOT NULL DEFAULT '{}'
);

-- Create conversion_rate_raw_data table with versioning
CREATE TABLE conversion_rate_raw_data (
    id TEXT NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    source_currency TEXT NOT NULL,
    target_currency TEXT NOT NULL,
    time_range TIMESTAMP NOT NULL,
    amount DECIMAL(20,8) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL,
    PRIMARY KEY (id, version),
    UNIQUE (source_currency, target_currency, id, version)
);

-- Create conversion_rate table with versioning
CREATE TABLE conversion_rate (
    id TEXT NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    brand_id TEXT NOT NULL REFERENCES brands(id),
    environment_id TEXT NOT NULL REFERENCES environments(id),
    status status NOT NULL DEFAULT 'ENABLED',
    source_currency TEXT NOT NULL,
    target_currency TEXT NOT NULL,
    value DECIMAL(20,8),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL,
    PRIMARY KEY (id, version),
    UNIQUE (source_currency, target_currency, brand_id, environment_id, id, version)
);

-- Create indexes for better performance
CREATE INDEX idx_fixer_api_currency_pairs ON fixer_api_currency_pairs(source_currency);

CREATE INDEX idx_conversion_rate_raw_data ON conversion_rate_raw_data(source_currency, target_currency, version);

CREATE INDEX idx_conversion_rate_brand_id ON conversion_rate(brand_id, environment_id);