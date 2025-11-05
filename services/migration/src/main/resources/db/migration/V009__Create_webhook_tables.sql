-- Migration: V009__Create_webhook_tables.sql
-- Description: Create webhook tables (webhooks, webhook_logs) for managing webhooks and their execution logs
-- Service: shared
-- Note: webhook_status_type and webhook_execution_status enums are already defined in V001__Create_enums.sql

-- Create webhooks table
CREATE TABLE webhooks (
    id TEXT PRIMARY KEY NOT NULL,
    status_type webhook_status_type NOT NULL,
    url TEXT NOT NULL,
    retry INTEGER NOT NULL DEFAULT 3,
    brand_id TEXT NOT NULL,
    environment_id TEXT NOT NULL,
    status status NOT NULL DEFAULT 'ENABLED',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL,

    CONSTRAINT fk_webhooks_brand_id FOREIGN KEY (brand_id) REFERENCES brands(id),
    CONSTRAINT fk_webhooks_environment_id FOREIGN KEY (environment_id) REFERENCES environments(id),
    CONSTRAINT uq_webhook_brand_status UNIQUE (brand_id, environment_id, status_type)
);

-- Create webhook_logs table
CREATE TABLE webhook_logs (
    id TEXT PRIMARY KEY,
    webhook_id TEXT NOT NULL,
    response_status INTEGER,
    is_success BOOLEAN,
    request_payload JSONB,
    response_payload JSONB,
    error_message TEXT,
    execution_time_ms BIGINT,
    attempt_number INTEGER NOT NULL DEFAULT 1,
    execution_status webhook_execution_status NOT NULL DEFAULT 'PENDING',
    scheduled_at TIMESTAMP,
    executed_at TIMESTAMP,
    completed_at TIMESTAMP,
    retry_after TIMESTAMP,
    job_id TEXT,
    correlation_id TEXT,
    user_agent TEXT,
    content_type TEXT,
    response_headers JSONB,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL,
    
    CONSTRAINT fk_webhook_logs_webhook_id FOREIGN KEY (webhook_id) REFERENCES webhooks(id)
);

-- Create indexes for better query performance
CREATE INDEX idx_webhooks_brand_id_environment_id ON webhooks(brand_id, environment_id);
CREATE INDEX idx_webhooks_status_type ON webhooks(status_type);
CREATE INDEX idx_webhooks_status ON webhooks(status);

CREATE INDEX idx_webhook_logs_webhook_id ON webhook_logs(webhook_id);
CREATE INDEX idx_webhook_logs_correlation_id ON webhook_logs(correlation_id);
CREATE INDEX idx_webhook_logs_execution_status ON webhook_logs(execution_status);
CREATE INDEX idx_webhook_logs_executed_at ON webhook_logs(executed_at);
CREATE INDEX idx_webhook_logs_job_id ON webhook_logs(job_id);