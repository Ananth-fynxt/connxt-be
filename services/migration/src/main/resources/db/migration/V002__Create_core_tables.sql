-- Migration: V002__Create_core_tables.sql
-- Description: Create core application tables (brands, environments, users, system users)
-- Service: shared

-- Create user table
CREATE TABLE users (
    id TEXT PRIMARY KEY NOT NULL,
    email TEXT NOT NULL,
    password TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL
);

-- Create system_users table for FYNXT system administrators
CREATE TABLE system_users (
    id TEXT PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    user_id TEXT NOT NULL REFERENCES users(id),
    role role_type NOT NULL,
    scope scope NOT NULL DEFAULT 'SYSTEM',
    status user_status NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL
);

-- Create brands table
CREATE TABLE brands (
    id TEXT PRIMARY KEY NOT NULL,
    name TEXT NOT NULL UNIQUE,
    email TEXT NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL
);

-- Create environments table
CREATE TABLE environments (
    id TEXT PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    secret TEXT NOT NULL UNIQUE,
    token TEXT NOT NULL UNIQUE,
    origin TEXT,
    success_redirect_url TEXT,
    failure_redirect_url TEXT,
    brand_id TEXT NOT NULL REFERENCES brands(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL
);

-- Create indexes for better query performance
CREATE INDEX idx_system_users_email ON system_users(email);
CREATE UNIQUE INDEX idx_system_users_name_email ON system_users(name, email);

CREATE UNIQUE INDEX idx_brands_name ON brands(name);
CREATE UNIQUE INDEX idx_brands_email ON brands(email);

CREATE INDEX idx_users_email ON users(email);

CREATE INDEX idx_environments_brand_id ON environments(brand_id);