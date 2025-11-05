-- Migration: V002__Create_core_tables.sql
-- Description: Create core application tables (brands, environments, flow_types, flow_actions, flow_targets, flow_definitions)
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
    scope scope NOT NULL DEFAULT 'SYSTEM',
    status user_status NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL
);

-- Create fi table (Financial Institution)
CREATE TABLE fi (
    id TEXT PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    user_id TEXT NOT NULL REFERENCES users(id),
    scope scope NOT NULL DEFAULT 'FI',
    status user_status NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL
);

-- Create brands table
CREATE TABLE brands (
    id TEXT PRIMARY KEY NOT NULL,
    fi_id TEXT NOT NULL REFERENCES fi(id),
    currencies TEXT[] NOT NULL,
    name TEXT NOT NULL,
    email TEXT NOT NULL,
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

-- Create flow_types table
CREATE TABLE flow_types (
    id TEXT PRIMARY KEY NOT NULL,
    name TEXT NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL
);

-- Create flow_actions table
CREATE TABLE flow_actions (
    id TEXT PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    steps TEXT[] NOT NULL,
    flow_type_id TEXT NOT NULL REFERENCES flow_types(id),
    input_schema JSONB NOT NULL,
    output_schema JSONB NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL
);

-- Create flow_targets table
CREATE TABLE flow_targets (
    id TEXT PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    logo TEXT NOT NULL,
    status status NOT NULL DEFAULT 'ENABLED',
    credential_schema JSONB NOT NULL,
    input_schema JSONB NOT NULL DEFAULT '{}',
    currencies TEXT[] NOT NULL DEFAULT '{}',
    countries TEXT[] NOT NULL DEFAULT '{}',
    payment_methods TEXT[] NOT NULL DEFAULT '{}',
    flow_type_id TEXT NOT NULL REFERENCES flow_types(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL
);

-- Create flow_definitions table
CREATE TABLE flow_definitions (
    id TEXT PRIMARY KEY NOT NULL,
    flow_action_id TEXT NOT NULL REFERENCES flow_actions(id),
    flow_target_id TEXT NOT NULL REFERENCES flow_targets(id),
    description TEXT,
    code TEXT NOT NULL,
    flow_configuration JSONB,
    brand_id TEXT REFERENCES brands(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL
);

-- Create indexes for better query performance
CREATE INDEX idx_system_users_email ON system_users(email);
CREATE UNIQUE INDEX idx_system_users_name_email ON system_users(name, email);

CREATE INDEX idx_fi_email ON fi(email);
CREATE UNIQUE INDEX idx_fi_name_email ON fi(name, email);

CREATE INDEX idx_brands_fi_id ON brands(fi_id);
CREATE UNIQUE INDEX idx_brands_fi_name ON brands(fi_id, name);

CREATE INDEX idx_environments_brand_id ON environments(brand_id);

CREATE UNIQUE INDEX flow_action_unique_constraint ON flow_actions(flow_type_id, name);
CREATE INDEX idx_flow_actions_flow_type_id ON flow_actions(flow_type_id);

CREATE UNIQUE INDEX flow_target_type_name ON flow_targets(flow_type_id, name);
CREATE INDEX idx_flow_targets_flow_type_id ON flow_targets(flow_type_id);

CREATE UNIQUE INDEX flow_definition_action_target ON flow_definitions(flow_action_id, flow_target_id);
CREATE INDEX idx_flow_definitions_flow_target_id ON flow_definitions(flow_target_id);
CREATE INDEX idx_flow_definitions_flow_action_id ON flow_definitions(flow_action_id);