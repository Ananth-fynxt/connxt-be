-- Migration: V010__Create_auth_tables.sql
-- Description: Create authentication tables (brand_users, system_roles)

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
CREATE UNIQUE INDEX idx_system_roles_name ON system_roles(name);

CREATE INDEX idx_brand_users_brand_id ON brand_users(brand_id, environment_id);
CREATE INDEX idx_brand_users_email ON brand_users(email);
CREATE UNIQUE INDEX idx_brand_users_brand_name_email ON brand_users(brand_id, environment_id, email);


