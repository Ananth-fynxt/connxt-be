-- Migration: V011__Create_auto_approval_tables.sql
-- Description: Create auto approval tables with versioning support (auto_approval, auto_approval_psps)
-- Service: shared

-- Create auto_approval table with versioning
CREATE TABLE auto_approval (
    id TEXT NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    name TEXT NOT NULL,
    currency TEXT NOT NULL,
    countries TEXT[] NOT NULL,
    brand_id TEXT NOT NULL REFERENCES brands(id),
    environment_id TEXT NOT NULL REFERENCES environments(id),
    flow_action_id TEXT NOT NULL REFERENCES flow_actions(id),
    max_amount DECIMAL(20,8) NOT NULL,
    status status NOT NULL DEFAULT 'ENABLED',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL,
    PRIMARY KEY (id, version)
);

-- Create auto_approval_psps table
CREATE TABLE auto_approval_psps (
    auto_approval_id TEXT NOT NULL,
    auto_approval_version INTEGER NOT NULL,
    psp_id TEXT NOT NULL REFERENCES psps(id),
    PRIMARY KEY (auto_approval_id, auto_approval_version, psp_id),
    CONSTRAINT fk_auto_approval_psps_auto_approval 
        FOREIGN KEY (auto_approval_id, auto_approval_version) 
        REFERENCES auto_approval(id, version)
);

-- Create indexes for better query performance
CREATE INDEX idx_auto_approval_brand_env ON auto_approval(brand_id, environment_id);

CREATE INDEX idx_auto_approval_psps_auto_approval ON auto_approval_psps(auto_approval_id, auto_approval_version);
