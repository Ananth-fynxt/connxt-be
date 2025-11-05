-- Migration: V015__Create_transaction_tables.sql
-- Description: Create transaction tables for write-ahead log (WAL) pattern
-- Service: core

-- Create transactions table with composite primary key for WAL pattern
CREATE TABLE transactions (
    brand_id TEXT NOT NULL,
    environment_id TEXT NOT NULL,
    txn_id TEXT NOT NULL,
    version INTEGER NOT NULL,
    request_id TEXT,
    flow_action_id TEXT,
    flow_target_id TEXT,
    psp_id TEXT,
    psp_txn_id TEXT,
    wallet_id TEXT,
    customer_id TEXT,
    customer_tag TEXT,
    customer_account_type TEXT,
    external_request_id TEXT,
    wallet_currency TEXT,
    transaction_type TEXT,
    status transaction_status,
    txn_currency TEXT,
    txn_fee DECIMAL(20,8),
    txn_amount DECIMAL(20,8),
    bo_approval_status TEXT,
    bo_approved_by TEXT,
    bo_approval_date TIMESTAMP,
    remarks TEXT,
    received_amount DECIMAL(20,8),
    received_currency TEXT,
    execute_payload JSONB,
    inserted_by_ip_address TEXT,
    updated_by_ip_address TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT,
    updated_by TEXT,

    PRIMARY KEY (brand_id, environment_id, txn_id, version)
);

-- Create indexes for better query performance
CREATE INDEX idx_transactions_brand_env_txn_version ON transactions(brand_id, environment_id, txn_id, version DESC);

CREATE INDEX idx_transactions_brand_env_customer_id ON transactions(brand_id, environment_id, customer_id);

CREATE INDEX idx_transactions_brand_env_transaction_type_status ON transactions(brand_id, environment_id, transaction_type, status);

CREATE INDEX idx_transactions_psp_risk_rules ON transactions (psp_id, brand_id, environment_id, flow_action_id, txn_currency, created_at) WHERE psp_id IS NOT NULL;

CREATE INDEX idx_transactions_customer_risk_rules ON transactions (customer_id, brand_id, environment_id, flow_action_id, txn_currency, created_at) WHERE customer_id IS NOT NULL;

CREATE INDEX idx_transactions_customer_tag_risk_rules ON transactions (customer_tag, brand_id, environment_id, flow_action_id, txn_currency, created_at) WHERE customer_tag IS NOT NULL;

CREATE INDEX idx_transactions_customer_account_type_risk_rules ON transactions (customer_account_type, brand_id, environment_id, flow_action_id, txn_currency, created_at) WHERE customer_account_type IS NOT NULL;

CREATE INDEX idx_transactions_status_created_at ON transactions (status, created_at);

CREATE INDEX idx_transactions_brand_env_time ON transactions (brand_id, environment_id, created_at);

CREATE INDEX idx_transactions_routing_calculations ON transactions (psp_id, brand_id, environment_id, flow_action_id, txn_currency, created_at) WHERE psp_id IS NOT NULL;

CREATE INDEX idx_transactions_routing_status ON transactions (status, created_at) WHERE status IN ('SUCCESS', 'COMPLETED');

CREATE INDEX idx_transactions_routing_composite ON transactions (brand_id, environment_id, flow_action_id, txn_currency, created_at, psp_id) WHERE psp_id IS NOT NULL;