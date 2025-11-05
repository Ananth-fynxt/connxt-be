-- Migration: V015__Create_transaction_tables.sql
-- Description: Create transaction tables for write-ahead log (WAL) pattern
-- Service: core

-- Create transactions table with composite primary key for WAL pattern
CREATE TABLE transactions (
    brand_id TEXT NOT NULL,
    environment_id TEXT NOT NULL,
    txn_id TEXT NOT NULL,
    version INTEGER NOT NULL,
    flow_action_id TEXT,
    flow_target_id TEXT,
    psp_id TEXT,
    psp_txn_id TEXT,
    transaction_type TEXT,
    status transaction_status,
    execute_payload JSONB,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT,
    updated_by TEXT,

    PRIMARY KEY (brand_id, environment_id, txn_id, version)
);

-- Create indexes for better query performance
-- Note: Primary key (brand_id, environment_id, txn_id, version) already provides an index

-- Index for finding latest version by txn_id (WAL pattern - most common lookup)
CREATE INDEX idx_transactions_txn_id_version ON transactions(txn_id, version DESC);

-- Index for brand + environment latest transactions (pagination queries)
CREATE INDEX idx_transactions_brand_env_latest ON transactions(brand_id, environment_id, created_at DESC);

-- Index for PSP-based queries (filtering by PSP)
CREATE INDEX idx_transactions_psp_id ON transactions(psp_id, created_at DESC) WHERE psp_id IS NOT NULL;

-- Index for PSP + brand + environment + flow queries
CREATE INDEX idx_transactions_psp_brand_env_flow ON transactions(psp_id, brand_id, environment_id, flow_action_id, created_at DESC) WHERE psp_id IS NOT NULL;

-- Index for flow action queries
CREATE INDEX idx_transactions_flow_action ON transactions(flow_action_id, brand_id, environment_id, created_at DESC) WHERE flow_action_id IS NOT NULL;

-- Index for status-based queries with time filtering
CREATE INDEX idx_transactions_status_created_at ON transactions(status, created_at DESC);

-- Index for transaction type and status filtering
CREATE INDEX idx_transactions_type_status ON transactions(transaction_type, status, created_at DESC) WHERE transaction_type IS NOT NULL;

-- Index for PSP transaction ID lookups
CREATE INDEX idx_transactions_psp_txn_id ON transactions(psp_txn_id) WHERE psp_txn_id IS NOT NULL;

-- Index for flow target queries
CREATE INDEX idx_transactions_flow_target ON transactions(flow_target_id, brand_id, environment_id, created_at DESC) WHERE flow_target_id IS NOT NULL;

-- Composite index for brand + environment + status + time (common filtering pattern)
CREATE INDEX idx_transactions_brand_env_status_time ON transactions(brand_id, environment_id, status, created_at DESC);