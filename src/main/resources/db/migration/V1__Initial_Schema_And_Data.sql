-- PostgreSQL Script for Financial Health Web Application
-- Version: 1.0
-- Description: Initializes the database schema and populates with initial data.
-- Flyway Naming: V1__Initial_Schema_And_Data.sql
-- WARNING: Contains DROP TABLE statements for easy re-run during development.
--          Do NOT run on a production database with existing data unless you intend to wipe it.
-- IMPORTANT: Dummy user passwords are using placeholder BCrypt hashes.
--            Replace these with actual, securely generated BCrypt hashes for any real use.

-- Drop Existing Tables (Idempotency for Development)
DROP TABLE IF EXISTS goals CASCADE;
DROP TABLE IF EXISTS budgets CASCADE;
DROP TABLE IF EXISTS transactions CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS roles CASCADE;

-- Data Definition Language (DDL)

-- roles table
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);
COMMENT ON TABLE roles IS 'Stores user roles (e.g., ROLE_USER, ROLE_ADMIN).';

-- users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE users IS 'Stores user account information.';
COMMENT ON COLUMN users.password_hash IS 'Stores BCrypt hashed passwords.';

-- user_roles join table
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);
COMMENT ON TABLE user_roles IS 'Join table for many-to-many relationship between users and roles.';

-- categories table
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE categories IS 'Stores predefined categories for transactions and budgets.';

-- transactions table
CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL, -- e.g., INCOME, EXPENSE
    amount NUMERIC(19, 4) NOT NULL,
    category_id BIGINT NOT NULL,
    transaction_date DATE NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT
);
COMMENT ON TABLE transactions IS 'Stores user financial transactions (income and expenses).';

-- budgets table
CREATE TABLE budgets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    allocated_amount NUMERIC(19, 4) NOT NULL,
    month VARCHAR(7) NOT NULL, -- Format: YYYY-MM
    total_monthly_budget_goal NUMERIC(19, 4),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, category_id, month),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT
);
COMMENT ON TABLE budgets IS 'Stores user-defined monthly budgets for categories.';
COMMENT ON COLUMN budgets.month IS 'Month in YYYY-MM format.';

-- goals table
CREATE TABLE goals (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    goal_name VARCHAR(255) NOT NULL,
    target_amount NUMERIC(19, 4) NOT NULL,
    current_amount NUMERIC(19, 4) NOT NULL DEFAULT 0.00,
    target_date DATE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
COMMENT ON TABLE goals IS 'Stores user-defined financial goals.';

-- Data Manipulation Language (DML)

-- Initial Roles
-- Assuming IDs will be 1 for ROLE_USER, 2 for ROLE_ADMIN due to BIGSERIAL
INSERT INTO roles (name) VALUES ('ROLE_USER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');

-- Initial Categories
-- Assuming IDs will start from 1 due to BIGSERIAL
INSERT INTO categories (name, description) VALUES ('Salary', 'Income from primary employment');
INSERT INTO categories (name, description) VALUES ('Groceries', 'Food and household supplies');
INSERT INTO categories (name, description) VALUES ('Utilities', 'Electricity, water, gas, internet bills');
INSERT INTO categories (name, description) VALUES ('Rent/Mortgage', 'Monthly housing payment');
INSERT INTO categories (name, description) VALUES ('Transportation', 'Fuel, public transport, vehicle maintenance');
INSERT INTO categories (name, description) VALUES ('Healthcare', 'Medical expenses, insurance');
INSERT INTO categories (name, description) VALUES ('Entertainment', 'Movies, dining out, hobbies');
INSERT INTO categories (name, description) VALUES ('Savings', 'Contributions to savings accounts');
INSERT INTO categories (name, description) VALUES ('Debt Payment', 'Credit card, loan payments');
INSERT INTO categories (name, description) VALUES ('Other Income', 'Freelance, bonuses, gifts received');
INSERT INTO categories (name, description) VALUES ('Other Expense', 'Miscellaneous expenses');

-- Dummy User Data
-- IMPORTANT: Replace placeholder BCrypt hashes with actual generated hashes for 'passwordUser' and 'passwordAdmin'.
-- Example: Generate hash using new BCryptPasswordEncoder().encode("passwordUser")

-- Dummy User 1 ('testuser', password: 'passwordUser')
-- Assuming user ID will be 1
INSERT INTO users (username, password_hash, created_at) VALUES
('testuser', '$2a$10$DUMMYHASHPLACEHOLDERUSER123456789012345678901234567890', NOW());
-- Note: The above hash is a correctly formatted length for BCrypt but is NOT a real hash for 'passwordUser'.

-- User Role (ROLE_USER)
-- Assuming user_id=1 (for testuser) and role_id=1 (for ROLE_USER)
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);

-- Transactions for testuser (user_id = 1)
-- Assuming category IDs: Salary=1, Groceries=2, Entertainment=7
INSERT INTO transactions (user_id, type, amount, category_id, transaction_date, description, created_at) VALUES
(1, 'INCOME', 2000.00, 1, CURRENT_DATE - INTERVAL '15 days', 'Monthly Salary', NOW()),
(1, 'EXPENSE', 150.00, 2, CURRENT_DATE - INTERVAL '10 days', 'Weekly Groceries', NOW()),
(1, 'EXPENSE', 75.00, 7, CURRENT_DATE - INTERVAL '5 days', 'Movie Night', NOW());

-- Budgets for testuser (user_id = 1)
-- Assuming category IDs: Groceries=2, Entertainment=7
INSERT INTO budgets (user_id, category_id, allocated_amount, month, total_monthly_budget_goal, created_at, updated_at) VALUES
(1, 2, 400.00, TO_CHAR(CURRENT_DATE, 'YYYY-MM'), 2000.00, NOW(), NOW()),
(1, 7, 150.00, TO_CHAR(CURRENT_DATE, 'YYYY-MM'), 2000.00, NOW(), NOW());

-- Goals for testuser (user_id = 1)
INSERT INTO goals (user_id, goal_name, target_amount, current_amount, target_date, created_at, updated_at) VALUES
(1, 'Save for Vacation', 1000.00, 250.00, CURRENT_DATE + INTERVAL '6 months', NOW(), NOW());


-- Dummy User 2 ('testadmin', password: 'passwordAdmin')
-- Assuming user ID will be 2
INSERT INTO users (username, password_hash, created_at) VALUES
('testadmin', '$2a$10$DUMMYHASHPLACEHOLDERADMIN12345678901234567890123456789', NOW());
-- Note: The above hash is a correctly formatted length for BCrypt but is NOT a real hash for 'passwordAdmin'.

-- User Roles (ROLE_USER, ROLE_ADMIN)
-- Assuming user_id=2 (for testadmin), role_id=1 (ROLE_USER), role_id=2 (ROLE_ADMIN)
INSERT INTO user_roles (user_id, role_id) VALUES (2, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (2, 2);

-- Transactions for testadmin (user_id = 2)
-- Assuming category IDs: Salary=1, Groceries=2, Utilities=3
INSERT INTO transactions (user_id, type, amount, category_id, transaction_date, description, created_at) VALUES
(2, 'INCOME', 3000.00, 1, CURRENT_DATE - INTERVAL '16 days', 'Admin Salary', NOW()),
(2, 'EXPENSE', 200.00, 2, CURRENT_DATE - INTERVAL '12 days', 'Admin Groceries', NOW()),
(2, 'EXPENSE', 100.00, 3, CURRENT_DATE - INTERVAL '7 days', 'Admin Utilities', NOW());

-- Budgets for testadmin (user_id = 2)
-- Assuming category ID: Groceries=2
INSERT INTO budgets (user_id, category_id, allocated_amount, month, total_monthly_budget_goal, created_at, updated_at) VALUES
(2, 2, 500.00, TO_CHAR(CURRENT_DATE, 'YYYY-MM'), 2500.00, NOW(), NOW());

-- Goals for testadmin (user_id = 2)
INSERT INTO goals (user_id, goal_name, target_amount, current_amount, target_date, created_at, updated_at) VALUES
(2, 'New Tech Gadget', 700.00, 100.00, CURRENT_DATE + INTERVAL '3 months', NOW(), NOW());

-- End of Script
