-- Migration: V001__Create_enums.sql
-- Description: Create all PostgreSQL enum types for the application
-- Service: shared

-- Create scope enum for system vs brand level permissions
CREATE TYPE scope AS ENUM ('SYSTEM', 'BRAND');

-- Create status enum for enabled/disabled states
CREATE TYPE status AS ENUM ('ENABLED', 'DISABLED');

-- Create token_type enum for different token purposes
CREATE TYPE token_type AS ENUM ('INVITATION', 'RESET_PASSWORD', 'ACCESS', 'REFRESH');

-- Create token_status enum for token status
CREATE TYPE token_status AS ENUM ('ACTIVE', 'REVOKED', 'EXPIRED');

-- Create user_status enum for user account states
CREATE TYPE user_status AS ENUM ('INVITED', 'ACTIVE', 'INACTIVE');

-- Create transaction_status enum for transaction state machine
CREATE TYPE transaction_status AS ENUM ('NEW', 'CREATED', 'HOLD_BALANCE', 'INITIATED', 'PG_ACCEPTED', 'PG_REJECTED', 'REJECTED', 'ABANDONED', 'PG_SUCCESS', 'PG_FAILED', 'PENDING_FOR_APPROVAL', 'APPROVED', 'AUTO_APPROVED', 'SUCCESS', 'FAILED', 'COMPLETED', 'CREDITED_TO_WALLET', 'CLEAR_HELD_BALANCE', 'CANCELLED');