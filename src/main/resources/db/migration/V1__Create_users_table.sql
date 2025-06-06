-- V1__Create_users_table.sql
-- Create users table with proper constraints and indexes

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    enabled BOOLEAN NOT NULL DEFAULT true,
    account_non_expired BOOLEAN NOT NULL DEFAULT true,
    account_non_locked BOOLEAN NOT NULL DEFAULT true,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create unique indexes for username and email
CREATE UNIQUE INDEX idx_user_username ON users (username);
CREATE UNIQUE INDEX idx_user_email ON users (email);

-- Insert default admin user (password: admin123)
-- Password is BCrypt hashed
INSERT INTO users (username, email, password, first_name, last_name)
VALUES ('admin', 'admin@example.com', '$2a$10$ywh1O2EwghHmFIMGeHgVYeu8roB0xNFxQ4PYzH4Ca2at.QP9.nMeC', 'System', 'Administrator');
