-- Insert single system user account (dummy password: d2ZZa17IF8ZGoVtL)
INSERT INTO users (id, email, password, created_at, updated_at, created_by, updated_by) VALUES
('usr_sys_admin', 'system.admin@connxt.com', 'Vlm9Fn9CHZDLlxvP70Hhusr1hBM5igoMtluuCL68RgGmXHhhBNFjXfZJzM4=', NOW(), NOW(), 'system', 'system');

-- Seed a default system role
INSERT INTO system_roles (id, name, permissions, created_at, updated_at, created_by, updated_by) VALUES
('sys_role_admin', 'System Administrator', '{}'::jsonb, NOW(), NOW(), 'system', 'system');

-- Link the system user to the role
INSERT INTO system_users (id, name, email, user_id, system_role_id, scope, status, created_at, updated_at, created_by, updated_by) VALUES
('sys_admin', 'System Administrator', 'system.admin@connxt.com', 'usr_sys_admin', 'sys_role_admin', 'SYSTEM', 'ACTIVE', NOW(), NOW(), 'system', 'system');