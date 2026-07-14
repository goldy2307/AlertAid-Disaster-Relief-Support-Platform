-- ===============================================
-- Alertaid Quick Database Setup
-- Simple schema based on your JPA entities
-- ===============================================

-- Create database
CREATE DATABASE IF NOT EXISTS alertaid_db;
USE alertaid_db;

-- Users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('CITIZEN', 'VOLUNTEER', 'ORG', 'ADMIN') NOT NULL DEFAULT 'CITIZEN',
    phone VARCHAR(15),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Campaign table
CREATE TABLE campaign (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    description VARCHAR(255),
    goal_amount DOUBLE NOT NULL,
    collected_amount DOUBLE NOT NULL
);

-- Donation table
CREATE TABLE donation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    donor_name VARCHAR(255),
    donor_email VARCHAR(255),
    amount DOUBLE NOT NULL,
    campaign_id BIGINT,
    FOREIGN KEY (campaign_id) REFERENCES campaign(id)
);

-- My contributions table
CREATE TABLE my_contributions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    contributor_name VARCHAR(255),
    email VARCHAR(255),
    contribution_type VARCHAR(255),
    details VARCHAR(255),
    amount DOUBLE
);

-- Volunteers table
CREATE TABLE volunteers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(255),
    address VARCHAR(255),
    skills VARCHAR(255),
    availability VARCHAR(255)
);

-- Seek for help table
CREATE TABLE seek_for_help (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(255),
    help_type VARCHAR(255),
    description VARCHAR(255)
);

-- Weather alerts table
CREATE TABLE weather_alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    description VARCHAR(255),
    severity VARCHAR(255),
    region VARCHAR(255),
    issued_at TIMESTAMP(6)
);

-- Insert a test admin user (password: 'admin123' - hashed)
INSERT INTO users (name, email, password, role, phone) VALUES 
('Admin User', 'admin@alertaid.com', '$2a$10$N.zmdr9k7uOCQb376NoUu.6cIvvdNzR4XNRK2JCkn7RvP8L9s8r4i', 'ADMIN', '1234567890');

SELECT 'Database setup complete!' as status;