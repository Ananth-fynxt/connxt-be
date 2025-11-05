-- Migration: V001__Create_enums.sql
-- Description: Create all PostgreSQL enum types for the application
-- Service: shared

-- Create scope enum for system vs brand level permissions
CREATE TYPE scope AS ENUM ('SYSTEM', 'FI', 'BRAND', 'EXTERNAL');

-- Create status enum for enabled/disabled states
CREATE TYPE status AS ENUM ('ENABLED', 'DISABLED');

-- Create token_type enum for different token purposes
CREATE TYPE token_type AS ENUM ('INVITATION', 'RESET_PASSWORD', 'ACCESS', 'REFRESH');

-- Create token_status enum for token status
CREATE TYPE token_status AS ENUM ('ACTIVE', 'REVOKED', 'EXPIRED');

-- Create user_status enum for user account states
CREATE TYPE user_status AS ENUM ('INVITED', 'ACTIVE', 'INACTIVE');

-- Create charge_fee_type enum for how fees are charged
CREATE TYPE charge_fee_type AS ENUM ('INCLUSIVE', 'EXCLUSIVE');

-- Create fee_component_type enum for component types
CREATE TYPE fee_component_type AS ENUM ('FIXED', 'PERCENTAGE');

-- Create risk_type enum for different risk rule types
CREATE TYPE risk_type AS ENUM ('DEFAULT', 'CUSTOMER');

-- Create risk_action enum for risk rule actions
CREATE TYPE risk_action AS ENUM ('BLOCK', 'ALERT');

-- Create risk_duration enum for time periods
CREATE TYPE risk_duration AS ENUM ('HOUR', 'DAY', 'WEEK', 'MONTH');

-- Create routing_duration enum for time periods
CREATE TYPE routing_duration AS ENUM ('HOUR', 'DAY', 'WEEK', 'MONTH');

-- Create routing_type enum for routing types
CREATE TYPE routing_type AS ENUM ('AMOUNT', 'PERCENTAGE', 'COUNT');

-- Create risk_customer_criteria_type enum for customer criteria
CREATE TYPE risk_customer_criteria_type AS ENUM ('TAG', 'ACCOUNT_TYPE');

-- Create psp_selection_mode enum for PSP selection strategies
CREATE TYPE psp_selection_mode AS ENUM ('PRIORITY', 'WEIGHTAGE');

-- Create webhook_status_type enum for webhook event types
CREATE TYPE webhook_status_type AS ENUM ('SUCCESS', 'FAILURE', 'NOTIFICATION');

-- Create transaction_status enum for transaction state machine
CREATE TYPE transaction_status AS ENUM ('NEW', 'CREATED', 'HOLD_BALANCE', 'INITIATED', 'PG_ACCEPTED', 'PG_REJECTED', 'REJECTED', 'ABANDONED', 'PG_SUCCESS', 'PG_FAILED', 'PENDING_FOR_APPROVAL', 'APPROVED', 'AUTO_APPROVED', 'SUCCESS', 'FAILED', 'COMPLETED', 'CREDITED_TO_WALLET', 'CLEAR_HELD_BALANCE', 'CANCELLED');

-- Create webhook_execution_status enum for webhook execution tracking
CREATE TYPE webhook_execution_status AS ENUM ('PENDING', 'IN_PROGRESS', 'SUCCESS', 'FAILED', 'RETRIED', 'CANCELLED');