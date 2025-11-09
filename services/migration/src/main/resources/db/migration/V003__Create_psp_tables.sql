-- Migration: V003__Create_psp_tables.sql
-- Description: Create PSP management tables (psps, psp_operations, maintenance_windows, psp_groups)
-- Service: shared

-- Create psps table
CREATE TABLE psps (
    id TEXT PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    logo TEXT,
    credential JSONB NOT NULL,
    brand_id TEXT NOT NULL REFERENCES brands(id),
    environment_id TEXT NOT NULL REFERENCES environments(id),
    flow_target_id TEXT NOT NULL REFERENCES flow_targets(id),
    status status NOT NULL DEFAULT 'ENABLED',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL
);

-- Create psp_operations table
CREATE TABLE psp_operations (
    psp_id TEXT NOT NULL REFERENCES psps(id),
    flow_action_id TEXT NOT NULL REFERENCES flow_actions(id),
    flow_definition_id TEXT NOT NULL REFERENCES flow_definitions(id),
    status status NOT NULL DEFAULT 'ENABLED',
    PRIMARY KEY (psp_id, flow_action_id, flow_definition_id)
);

-- Create indexes for better query performance
CREATE UNIQUE INDEX idx_psp_brand_env_flow_target_name ON psps(brand_id, environment_id, flow_target_id, name);

CREATE INDEX idx_psp_brand_env ON psps(brand_id, environment_id);

CREATE INDEX idx_psp_operations_psp_id ON psp_operations(psp_id);