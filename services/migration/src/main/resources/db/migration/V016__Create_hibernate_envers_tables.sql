-- Migration: V014__Create_hibernate_envers_tables.sql
-- Description: Create Hibernate Envers auditing tables for entity versioning
-- Service: core

-- Create revinfo table for Hibernate Envers
-- This table stores revision information for audited entities
CREATE TABLE revinfo (
    rev INTEGER NOT NULL,
    revtstmp BIGINT,
    PRIMARY KEY (rev)
);

-- Create sequence for revinfo table
-- This sequence generates revision numbers for Envers
-- Note: Hibernate Envers expects increment size of 50 by default
-- Use DO block to create sequence only if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_sequences WHERE sequencename = 'revinfo_seq') THEN
        CREATE SEQUENCE revinfo_seq
            START WITH 1
            INCREMENT BY 50
            MINVALUE 1
            NO MAXVALUE
            CACHE 1;
    END IF;
END $$;

-- Ensure the sequence starts from 1 and is properly initialized
-- This prevents negative revision numbers
SELECT setval('revinfo_seq', 1, false);

-- Create audit table for psps entity
-- This table stores historical versions of psp records
CREATE TABLE psps_aud (
    id TEXT NOT NULL,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    name TEXT,
    description TEXT,
    logo TEXT,
    credential JSONB,
    timeout INTEGER,
    block_vpn_access BOOLEAN,
    block_data_center_access BOOLEAN,
    failure_rate BOOLEAN,
    ip_address TEXT[],
    brand_id TEXT,
    environment_id TEXT,
    flow_target_id TEXT,
    status status,
    failure_rate_threshold REAL,
    failure_rate_duration_minutes INTEGER,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by TEXT,
    updated_by TEXT,
    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo(rev)
);

-- Create audit table for psp_operations entity
-- This table stores historical versions of psp operation records
CREATE TABLE psp_operations_aud (
    psp_id TEXT NOT NULL,
    flow_action_id TEXT NOT NULL,
    flow_definition_id TEXT NOT NULL,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    currencies TEXT[],
    countries TEXT[],
    status status,
    PRIMARY KEY (psp_id, flow_action_id, flow_definition_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo(rev)
);

-- Create audit table for maintenance_windows entity
-- This table stores historical versions of maintenance window records
CREATE TABLE maintenance_windows_aud (
    id TEXT NOT NULL,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    psp_id TEXT,
    flow_action_id TEXT,
    start_at TIMESTAMP,
    end_at TIMESTAMP,
    status status,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by TEXT,
    updated_by TEXT,
    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo(rev)
);

-- Create indexes for better query performance on audit tables
CREATE INDEX idx_psps_aud_rev ON psps_aud(rev);
CREATE INDEX idx_psps_aud_revtype ON psps_aud(revtype);
CREATE INDEX idx_psps_aud_brand_id ON psps_aud(brand_id);
CREATE INDEX idx_psps_aud_environment_id ON psps_aud(environment_id);

CREATE INDEX idx_psp_operations_aud_rev ON psp_operations_aud(rev);
CREATE INDEX idx_psp_operations_aud_revtype ON psp_operations_aud(revtype);
CREATE INDEX idx_psp_operations_aud_psp_id ON psp_operations_aud(psp_id);

CREATE INDEX idx_maintenance_windows_aud_rev ON maintenance_windows_aud(rev);
CREATE INDEX idx_maintenance_windows_aud_revtype ON maintenance_windows_aud(revtype);
CREATE INDEX idx_maintenance_windows_aud_psp_id ON maintenance_windows_aud(psp_id);
