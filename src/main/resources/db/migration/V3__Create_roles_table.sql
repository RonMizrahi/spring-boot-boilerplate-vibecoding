-- V3__Create_roles_table.sql
-- Create roles table with proper constraints and indexes

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create unique index for role name
CREATE UNIQUE INDEX idx_role_name ON roles (name);

-- Insert default roles
INSERT INTO roles (name, description) VALUES 
('ADMIN', 'Administrator with full access'),
('USER', 'Regular user with standard privileges'),
('GUEST', 'Guest user with limited access');
