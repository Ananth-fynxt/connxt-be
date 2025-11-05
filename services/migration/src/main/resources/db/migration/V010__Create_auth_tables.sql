-- Migration: V010__Create_auth_tables.sql
-- Description: Create authentication tables (brand_users, brand_roles)

-- Create brand_roles table
CREATE TABLE brand_roles (
    id TEXT PRIMARY KEY NOT NULL,
    brand_id TEXT NOT NULL REFERENCES brands(id),
    environment_id TEXT NOT NULL REFERENCES environments(id),
    name TEXT NOT NULL,
    permission JSONB NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL
);

-- Create brand_users table
CREATE TABLE brand_users (
    id TEXT PRIMARY KEY NOT NULL,
    brand_id TEXT REFERENCES brands(id),
    environment_id TEXT REFERENCES environments(id),
    brand_role_id TEXT REFERENCES brand_roles(id),
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    user_id TEXT NOT NULL REFERENCES users(id),
    scope scope NOT NULL DEFAULT 'BRAND',
    status user_status NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL
);

-- Create tokens table
CREATE TABLE tokens (
    id TEXT PRIMARY KEY NOT NULL,
    customer_id TEXT NOT NULL,
    token_hash TEXT NOT NULL,
    issued_at TIMESTAMP WITH TIME ZONE NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    status token_status NOT NULL DEFAULT 'ACTIVE',
    token_type token_type NOT NULL DEFAULT 'ACCESS',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL
);

-- Create indexes for performance
CREATE INDEX idx_tokens_customer_id ON tokens(customer_id);

CREATE INDEX idx_users_email ON users(email);

-- Create indexes for better query performance
CREATE INDEX idx_brand_roles_brand_id ON brand_roles(brand_id, environment_id);
CREATE UNIQUE INDEX idx_brand_roles_brand_name_permission ON brand_roles(brand_id, environment_id, name);

CREATE INDEX idx_brand_users_brand_id ON brand_users(brand_id, environment_id);
CREATE INDEX idx_brand_users_email ON brand_users(email);
CREATE UNIQUE INDEX idx_brand_users_brand_name_email ON brand_users(brand_id, environment_id, email);


