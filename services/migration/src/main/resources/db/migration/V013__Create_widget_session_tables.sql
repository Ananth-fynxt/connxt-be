-- Create widget_sessions table
CREATE TABLE widget_sessions (
    id TEXT PRIMARY KEY,
    brand_id TEXT NOT NULL,
    environment_id TEXT NOT NULL,
    customer_id TEXT NOT NULL,
    session_token_hash TEXT NOT NULL,
    fingerprint_hash TEXT,
    fingerprint JSONB,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_refreshed_at TIMESTAMP WITH TIME ZONE,
    last_accessed_at TIMESTAMP WITH TIME ZONE,
    extension_count INTEGER DEFAULT 0,
    max_extensions INTEGER DEFAULT 3,
    timeout_minutes INTEGER DEFAULT 60,
    auto_extend BOOLEAN DEFAULT true,
    revoked BOOLEAN DEFAULT false,
    revoked_by TEXT,
    revoked_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL
);

-- Create indexes for better performance
CREATE INDEX idx_widget_sessions_customer_brand_env ON widget_sessions (customer_id, brand_id, environment_id);
