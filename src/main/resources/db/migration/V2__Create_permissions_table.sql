-- V2__Create_permissions_table.sql
-- Create permissions table with proper constraints and indexes

CREATE TABLE permissions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    resource VARCHAR(50),
    action VARCHAR(50),
    enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create unique index for permission name
CREATE UNIQUE INDEX idx_permission_name ON permissions (name);

-- Insert base permissions
INSERT INTO permissions (name, description, resource, action) VALUES 
('USER_READ', 'Read user information', 'USER', 'READ'),
('USER_CREATE', 'Create new users', 'USER', 'CREATE'),
('USER_UPDATE', 'Update existing users', 'USER', 'UPDATE'),
('USER_DELETE', 'Delete users', 'USER', 'DELETE'),
('PROFILE_READ', 'Read user profiles', 'PROFILE', 'READ'),
('PROFILE_CREATE', 'Create user profiles', 'PROFILE', 'CREATE'),
('PROFILE_UPDATE', 'Update user profiles', 'PROFILE', 'UPDATE'),
('PROFILE_DELETE', 'Delete user profiles', 'PROFILE', 'DELETE'),
('ADMIN_ACCESS', 'Access admin features', 'ADMIN', 'ACCESS');
