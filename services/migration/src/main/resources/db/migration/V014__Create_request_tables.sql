-- Migration: V014__Create_request_tables.sql
-- Description: Create request tables for request management system
-- Service: core

CREATE TABLE requests (
    id TEXT NOT NULL,
    brand_id TEXT NOT NULL,
    environment_id TEXT NOT NULL,
    customer_id TEXT NOT NULL,
    wallet_id TEXT,
    customer_tag TEXT,
    customer_account_type TEXT,
    flow_action_id TEXT NOT NULL,
    amount DECIMAL(20,8),
    currency TEXT,
    country TEXT,
    routing_rule_id TEXT,
    routing_rule_version INTEGER,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL,

    CONSTRAINT requests_pk PRIMARY KEY (id),
    CONSTRAINT fk_requests_brand_id FOREIGN KEY (brand_id) REFERENCES brands(id),
    CONSTRAINT fk_requests_environment_id FOREIGN KEY (environment_id) REFERENCES environments(id),
    CONSTRAINT fk_requests_customer_id FOREIGN KEY (customer_id) REFERENCES brand_customer(id),
    CONSTRAINT fk_requests_flow_action_id FOREIGN KEY (flow_action_id) REFERENCES flow_actions(id),
    CONSTRAINT fk_requests_routing_rule_id FOREIGN KEY (routing_rule_id, routing_rule_version) REFERENCES routing_rules(id, version)
);

CREATE TABLE request_psps (
    request_id TEXT NOT NULL,
    psp_id TEXT NOT NULL,
    flow_target_id TEXT NOT NULL,
    flow_definition_id TEXT NOT NULL,
    currency TEXT NOT NULL,
    original_amount DECIMAL(20,8) NOT NULL,
    applied_fee_amount DECIMAL(20,8),
    total_amount DECIMAL(20,8) NOT NULL,
    conversion_from_currency TEXT,
    conversion_to_currency TEXT,
    conversion_exchange_rate DECIMAL(20,8),
    conversion_converted_amount DECIMAL(20,8),
    is_conversion_applied BOOLEAN DEFAULT FALSE,
    net_amount_to_user DECIMAL(20,8),
    inclusive_fee_amount DECIMAL(20,8),
    exclusive_fee_amount DECIMAL(20,8),
    is_fee_applied BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL,

    CONSTRAINT request_psps_pk PRIMARY KEY (request_id, psp_id),
    CONSTRAINT fk_request_psps_request_id FOREIGN KEY (request_id) REFERENCES requests(id),
    CONSTRAINT fk_request_psps_psp_id FOREIGN KEY (psp_id) REFERENCES psps(id),
    CONSTRAINT fk_request_psps_flow_target_id FOREIGN KEY (flow_target_id) REFERENCES flow_targets(id),
    CONSTRAINT fk_request_psps_flow_definition_id FOREIGN KEY (flow_definition_id) REFERENCES flow_definitions(id)
);

CREATE TABLE request_transaction_limits (
    request_id TEXT NOT NULL,
    transaction_limit_id TEXT NOT NULL,
    transaction_limit_version INTEGER NOT NULL,

    CONSTRAINT request_transaction_limits_pk PRIMARY KEY (request_id, transaction_limit_id, transaction_limit_version),
    CONSTRAINT fk_request_transaction_limits_request_id FOREIGN KEY (request_id) REFERENCES requests(id),
    CONSTRAINT fk_request_transaction_limits_transaction_limit_id FOREIGN KEY (transaction_limit_id, transaction_limit_version) REFERENCES transaction_limits(id, version)
);

CREATE TABLE request_risk_rules (
    request_id TEXT NOT NULL,
    risk_rule_id TEXT NOT NULL,
    risk_rule_version INTEGER NOT NULL,

    CONSTRAINT request_risk_rules_pk PRIMARY KEY (request_id, risk_rule_id, risk_rule_version),
    CONSTRAINT fk_request_risk_rules_request_id FOREIGN KEY (request_id) REFERENCES requests(id),
    CONSTRAINT fk_request_risk_rules_risk_rule_id FOREIGN KEY (risk_rule_id, risk_rule_version) REFERENCES risk_rule(id, version)
);

CREATE TABLE request_fees (
    request_id TEXT NOT NULL,
    fee_id TEXT NOT NULL,
    fee_version INTEGER NOT NULL,

    CONSTRAINT request_fees_pk PRIMARY KEY (request_id, fee_id, fee_version),
    CONSTRAINT fk_request_fees_request_id FOREIGN KEY (request_id) REFERENCES requests(id),
    CONSTRAINT fk_request_fees_fee_id FOREIGN KEY (fee_id, fee_version) REFERENCES fee(id, version)
);

-- Create indexes for better query performance
CREATE INDEX idx_requests_brand_env_customer_wallet ON requests(brand_id, environment_id, customer_id, wallet_id);

CREATE INDEX idx_requests_brand_env_customer ON requests(brand_id, environment_id, customer_id);
