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

