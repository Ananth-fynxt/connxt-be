CREATE TABLE transaction_limits (
    id TEXT NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    name TEXT NOT NULL,
    brand_id TEXT NOT NULL REFERENCES brands(id),
    environment_id TEXT NOT NULL REFERENCES environments(id),
    currency TEXT NOT NULL,
    countries TEXT[] NOT NULL,
    customer_tags TEXT[] NOT NULL,
    status status NOT NULL DEFAULT 'ENABLED',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL,
    PRIMARY KEY (id, version)
);

CREATE TABLE transaction_limit_psps_actions (
    transaction_limit_id TEXT NOT NULL,
    transaction_limit_version INTEGER NOT NULL,
    flow_action_id TEXT NOT NULL REFERENCES flow_actions(id),
    min_amount DECIMAL(20,8) NOT NULL,
    max_amount DECIMAL(20,8) NOT NULL,
    PRIMARY KEY (transaction_limit_id, transaction_limit_version, flow_action_id),
    FOREIGN KEY (transaction_limit_id, transaction_limit_version) REFERENCES transaction_limits(id, version)
);

CREATE TABLE transaction_limit_psps (
    transaction_limit_id TEXT NOT NULL,
    transaction_limit_version INTEGER NOT NULL,
    psp_id TEXT NOT NULL REFERENCES psps(id),
    PRIMARY KEY (transaction_limit_id, transaction_limit_version, psp_id),
    FOREIGN KEY (transaction_limit_id, transaction_limit_version) REFERENCES transaction_limits(id, version)
);

CREATE INDEX idx_transaction_limit_psps_transaction_limit ON transaction_limit_psps(transaction_limit_id, transaction_limit_version);

CREATE INDEX idx_transaction_limit_psps_actions_transaction_limit ON transaction_limit_psps_actions(transaction_limit_id, transaction_limit_version);

CREATE INDEX idx_transaction_limits_brand_env ON transaction_limits(brand_id, environment_id);