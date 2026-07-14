-- ===============================================
-- Alertaid Database Schema for MySQL
-- Generated from Spring Boot JPA Entities
-- ===============================================

-- Create database
CREATE DATABASE IF NOT EXISTS alertaid_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE alertaid_db;

-- ===============================================
-- Table: users
-- Stores user authentication and profile data
-- ===============================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('CITIZEN', 'VOLUNTEER', 'ORG', 'ADMIN') NOT NULL DEFAULT 'CITIZEN',
    phone VARCHAR(15),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================
-- Table: campaign
-- Enhanced fundraising campaign information with new features
-- ===============================================
CREATE TABLE IF NOT EXISTS campaign (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    goal_amount DOUBLE NOT NULL DEFAULT 0.0,
    collected_amount DOUBLE NOT NULL DEFAULT 0.0,
    campaign_type ENUM('PERSONAL', 'MEDICAL', 'COMMUNITY', 'BUSINESS', 'EMERGENCY', 'OTHER') DEFAULT 'PERSONAL',
    beneficiary_name VARCHAR(255),
    organizer_name VARCHAR(255),
    organizer_email VARCHAR(255),
    organizer_phone VARCHAR(20),
    account_holder_name VARCHAR(255),
    account_number VARCHAR(50),
    bank_name VARCHAR(255),
    ifsc_code VARCHAR(20),
    upi_id VARCHAR(100),
    qr_code_image TEXT COMMENT 'Base64 encoded QR code image or file path',
    beneficiary_image TEXT COMMENT 'Base64 encoded beneficiary image or file path',
    story_images TEXT COMMENT 'JSON array of image paths/base64',
    fund_usage TEXT COMMENT 'Detailed breakdown of fund usage',
    status ENUM('ACTIVE', 'COMPLETED', 'PAUSED', 'CANCELLED') DEFAULT 'ACTIVE',
    duration_days INT DEFAULT 90,
    end_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_title (title),
    INDEX idx_goal_amount (goal_amount),
    INDEX idx_campaign_type (campaign_type),
    INDEX idx_status (status),
    INDEX idx_organizer_email (organizer_email),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================
-- Table: donation
-- Enhanced donation tracking with payment details
-- ===============================================
CREATE TABLE IF NOT EXISTS donation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    donor_name VARCHAR(255) NOT NULL,
    donor_email VARCHAR(255) NOT NULL,
    donor_phone VARCHAR(20),
    amount DOUBLE NOT NULL,
    currency VARCHAR(10) DEFAULT 'INR',
    payment_method ENUM('CARD', 'UPI', 'NETBANKING', 'WALLET') NOT NULL,
    payment_status ENUM('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED') DEFAULT 'PENDING',
    transaction_id VARCHAR(100),
    payment_reference VARCHAR(100),
    is_recurring BOOLEAN DEFAULT FALSE,
    recurring_frequency ENUM('MONTHLY', 'QUARTERLY', 'YEARLY'),
    message TEXT COMMENT 'Optional message from donor',
    is_anonymous BOOLEAN DEFAULT FALSE,
    campaign_id BIGINT,
    user_id BIGINT COMMENT 'If donor is registered user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (campaign_id) REFERENCES campaign(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_donor_email (donor_email),
    INDEX idx_amount (amount),
    INDEX idx_payment_method (payment_method),
    INDEX idx_payment_status (payment_status),
    INDEX idx_campaign_id (campaign_id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================
-- Table: my_contributions
-- Stores user contributions (money, food, clothes, medicines)
-- ===============================================
CREATE TABLE IF NOT EXISTS my_contributions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    contributor_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    contribution_type VARCHAR(255) NOT NULL COMMENT 'e.g., Money, Food, Clothes, Medicines',
    details TEXT COMMENT 'Additional details about the contribution',
    amount DOUBLE COMMENT 'For monetary contributions',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_contribution_type (contribution_type),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================
-- Table: volunteers
-- Stores volunteer registration information
-- ===============================================
CREATE TABLE IF NOT EXISTS volunteers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    skills TEXT COMMENT 'e.g., First Aid, Rescue, Logistics',
    availability VARCHAR(255) COMMENT 'e.g., Weekends, Anytime, Night shift',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_skills (skills(100)),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================
-- Table: seek_for_help
-- Stores help requests from people in need
-- ===============================================
CREATE TABLE IF NOT EXISTS seek_for_help (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    help_type VARCHAR(255) NOT NULL COMMENT 'e.g., food, shelter, rescue, medical',
    description TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED') DEFAULT 'OPEN',
    INDEX idx_email (email),
    INDEX idx_help_type (help_type),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================
-- Table: weather_alerts
-- Stores weather and disaster alerts by region
-- ===============================================
CREATE TABLE IF NOT EXISTS weather_alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL COMMENT 'e.g., Heavy Rainfall Warning',
    description TEXT NOT NULL,
    severity ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') NOT NULL DEFAULT 'MEDIUM',
    region VARCHAR(255) NOT NULL COMMENT 'Affected area/region',
    issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NULL COMMENT 'When the alert expires',
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_severity (severity),
    INDEX idx_region (region),
    INDEX idx_issued_at (issued_at),
    INDEX idx_expires_at (expires_at),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================
-- Additional useful tables for enhanced functionality
-- ===============================================

-- Table: user_sessions (for better session management)
CREATE TABLE IF NOT EXISTS user_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    session_token VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_session_token (session_token),
    INDEX idx_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: notification_log (for tracking notifications sent)
CREATE TABLE IF NOT EXISTS notification_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    notification_type ENUM('EMAIL', 'SMS', 'PUSH', 'IN_APP') NOT NULL,
    subject VARCHAR(255),
    message TEXT NOT NULL,
    recipient VARCHAR(255) NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'SENT', 'FAILED', 'BOUNCED') DEFAULT 'PENDING',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_notification_type (notification_type),
    INDEX idx_sent_at (sent_at),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================
-- Initial Data / Seed Data
-- ===============================================

-- Insert default admin user (password should be hashed in real application)
INSERT IGNORE INTO users (name, email, password, role, phone, created_at) VALUES 
('Admin User', 'admin@alertaid.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqyc1R5lC8n6J1k8w5wqK.C', 'ADMIN', '1234567890', NOW()),
('Test Citizen', 'citizen@test.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqyc1R5lC8n6J1k8w5wqK.C', 'CITIZEN', '9876543210', NOW());

-- Insert sample weather alert
INSERT IGNORE INTO weather_alerts (title, description, severity, region, issued_at, expires_at) VALUES 
('Heavy Rainfall Warning', 'Heavy rainfall expected in the region for the next 24 hours. Stay indoors and avoid unnecessary travel.', 'HIGH', 'Mumbai Metropolitan Region', NOW(), DATE_ADD(NOW(), INTERVAL 24 HOUR));

-- Insert enhanced sample campaigns with new fields
INSERT IGNORE INTO campaign (title, description, goal_amount, collected_amount, campaign_type, organizer_name, organizer_email, beneficiary_name, account_holder_name, account_number, bank_name, ifsc_code, upi_id, fund_usage, status, duration_days, end_date) VALUES 
('Flood Relief Fund', 'Emergency fund to help flood victims with food, shelter and medical aid.', 100000.00, 25000.00, 'EMERGENCY', 'Relief Committee', 'relief@alertaid.com', 'Flood Affected Families', 'AlertAid Relief Fund', '1234567890123456', 'State Bank of India', 'SBIN0001234', 'relief@paytm', '60% Food & Water, 25% Shelter, 10% Medical Aid, 5% Administrative', 'ACTIVE', 60, DATE_ADD(CURDATE(), INTERVAL 60 DAY)),
('Education for Underprivileged', 'Supporting education for children from low-income families.', 50000.00, 15000.00, 'COMMUNITY', 'Education Foundation', 'education@alertaid.com', 'Underprivileged Children', 'AlertAid Education Fund', '9876543210987654', 'HDFC Bank', 'HDFC0001234', 'education@paytm', '70% Books & Supplies, 20% School Fees, 10% Infrastructure', 'ACTIVE', 90, DATE_ADD(CURDATE(), INTERVAL 90 DAY));

-- ===============================================
-- Useful Views for Reporting
-- ===============================================

-- View: Enhanced active campaigns with donation summary
CREATE OR REPLACE VIEW active_campaigns_summary AS
SELECT 
    c.id,
    c.title,
    c.description,
    c.goal_amount,
    c.collected_amount,
    c.campaign_type,
    c.beneficiary_name,
    c.organizer_name,
    c.status,
    c.end_date,
    DATEDIFF(c.end_date, CURDATE()) as days_remaining,
    ROUND((c.collected_amount / c.goal_amount * 100), 2) as completion_percentage,
    COUNT(d.id) as total_donations,
    COUNT(DISTINCT d.donor_email) as unique_donors,
    AVG(d.amount) as average_donation,
    MAX(d.created_at) as last_donation_date
FROM campaign c
LEFT JOIN donation d ON c.id = d.campaign_id AND d.payment_status = 'SUCCESS'
WHERE c.status = 'ACTIVE'
GROUP BY c.id, c.title, c.description, c.goal_amount, c.collected_amount, c.campaign_type, c.beneficiary_name, c.organizer_name, c.status, c.end_date;

-- View: Help requests summary by type
CREATE OR REPLACE VIEW help_requests_summary AS
SELECT 
    help_type,
    COUNT(*) as total_requests,
    SUM(CASE WHEN status = 'OPEN' THEN 1 ELSE 0 END) as open_requests,
    SUM(CASE WHEN status = 'RESOLVED' THEN 1 ELSE 0 END) as resolved_requests
FROM seek_for_help
GROUP BY help_type;

-- View: Active weather alerts
CREATE OR REPLACE VIEW active_weather_alerts AS
SELECT *
FROM weather_alerts
WHERE is_active = TRUE 
AND (expires_at IS NULL OR expires_at > NOW())
ORDER BY severity DESC, issued_at DESC;

-- ===============================================
-- Stored Procedures for Common Operations
-- ===============================================

-- Procedure to update campaign collected amount when donation is made
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS UpdateCampaignAmount(
    IN campaign_id_param BIGINT,
    IN donation_amount DOUBLE
)
BEGIN
    UPDATE campaign 
    SET collected_amount = collected_amount + donation_amount,
        updated_at = NOW()
    WHERE id = campaign_id_param;
END //
DELIMITER ;

-- Procedure to get active campaigns with statistics
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS GetActiveCampaignsWithStats()
BEGIN
    SELECT 
        c.*,
        COUNT(d.id) as donation_count,
        AVG(d.amount) as avg_donation,
        MAX(d.created_at) as last_donation_date,
        DATEDIFF(c.end_date, CURDATE()) as days_remaining,
        ROUND((c.collected_amount / c.goal_amount) * 100, 2) as completion_percentage
    FROM campaign c
    LEFT JOIN donation d ON c.id = d.campaign_id AND d.payment_status = 'SUCCESS'
    WHERE c.status = 'ACTIVE'
    AND (c.end_date IS NULL OR c.end_date >= CURDATE())
    GROUP BY c.id
    ORDER BY c.created_at DESC;
END //
DELIMITER ;

-- Procedure to get user donation history
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS GetUserDonationHistory(
    IN user_email VARCHAR(255)
)
BEGIN
    SELECT 
        d.*,
        c.title as campaign_title,
        c.campaign_type
    FROM donation d
    LEFT JOIN campaign c ON d.campaign_id = c.id
    WHERE d.donor_email = user_email
    ORDER BY d.created_at DESC;
END //
DELIMITER ;

-- Procedure to get real-time donation stats
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS GetRealTimeDonationStats()
BEGIN
    SELECT 
        COUNT(DISTINCT d.id) as total_donations,
        SUM(d.amount) as total_amount_raised,
        COUNT(DISTINCT d.donor_email) as unique_donors,
        COUNT(DISTINCT c.id) as active_campaigns,
        AVG(d.amount) as average_donation
    FROM donation d
    LEFT JOIN campaign c ON d.campaign_id = c.id
    WHERE d.payment_status = 'SUCCESS'
    AND c.status = 'ACTIVE';
END //
DELIMITER ;

-- ===============================================
-- Triggers for Data Integrity
-- ===============================================

-- Trigger to automatically update campaign collected amount on successful donation
DELIMITER //
CREATE TRIGGER IF NOT EXISTS after_donation_insert
AFTER INSERT ON donation
FOR EACH ROW
BEGIN
    IF NEW.payment_status = 'SUCCESS' THEN
        UPDATE campaign 
        SET collected_amount = collected_amount + NEW.amount,
            updated_at = NOW()
        WHERE id = NEW.campaign_id;
    END IF;
END //
DELIMITER ;

-- Trigger to update campaign amount when donation payment status changes
DELIMITER //
CREATE TRIGGER IF NOT EXISTS after_donation_update
AFTER UPDATE ON donation
FOR EACH ROW
BEGIN
    -- Handle payment status changes
    IF OLD.payment_status != NEW.payment_status THEN
        IF OLD.payment_status = 'SUCCESS' AND NEW.payment_status != 'SUCCESS' THEN
            -- Donation was successful, now failed/refunded - subtract amount
            UPDATE campaign 
            SET collected_amount = collected_amount - OLD.amount,
                updated_at = NOW()
            WHERE id = NEW.campaign_id;
        ELSEIF OLD.payment_status != 'SUCCESS' AND NEW.payment_status = 'SUCCESS' THEN
            -- Donation was not successful, now successful - add amount
            UPDATE campaign 
            SET collected_amount = collected_amount + NEW.amount,
                updated_at = NOW()
            WHERE id = NEW.campaign_id;
        END IF;
    END IF;
    
    -- Handle campaign changes (only for successful donations)
    IF OLD.campaign_id != NEW.campaign_id AND NEW.payment_status = 'SUCCESS' THEN
        -- Remove from old campaign
        UPDATE campaign 
        SET collected_amount = collected_amount - NEW.amount,
            updated_at = NOW()
        WHERE id = OLD.campaign_id;
        
        -- Add to new campaign
        UPDATE campaign 
        SET collected_amount = collected_amount + NEW.amount,
            updated_at = NOW()
        WHERE id = NEW.campaign_id;
    END IF;
END //
DELIMITER ;

-- Trigger to update campaign amount when successful donation is deleted
DELIMITER //
CREATE TRIGGER IF NOT EXISTS after_donation_delete
AFTER DELETE ON donation
FOR EACH ROW
BEGIN
    IF OLD.payment_status = 'SUCCESS' THEN
        UPDATE campaign 
        SET collected_amount = collected_amount - OLD.amount,
            updated_at = NOW()
        WHERE id = OLD.campaign_id;
    END IF;
END //
DELIMITER ;

-- ===============================================
-- Performance Optimizations
-- ===============================================

-- Additional indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_users_role_created ON users(role, created_at);
CREATE INDEX IF NOT EXISTS idx_donations_campaign_amount ON donation(campaign_id, amount);
CREATE INDEX IF NOT EXISTS idx_donations_payment_status_created ON donation(payment_status, created_at);
CREATE INDEX IF NOT EXISTS idx_contributions_type_date ON my_contributions(contribution_type, created_at);
CREATE INDEX IF NOT EXISTS idx_weather_alerts_region_active ON weather_alerts(region, is_active, severity);
CREATE INDEX IF NOT EXISTS idx_campaigns_status_type ON campaign(status, campaign_type);
CREATE INDEX IF NOT EXISTS idx_campaigns_end_date ON campaign(end_date);

-- ===============================================
-- Database User Management (Optional)
-- ===============================================

-- Create application user with limited privileges (uncomment if needed)
-- CREATE USER IF NOT EXISTS 'alertaid_app'@'localhost' IDENTIFIED BY 'secure_password_here';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON alertaid_db.* TO 'alertaid_app'@'localhost';
-- FLUSH PRIVILEGES;

-- ===============================================
-- Schema Information
-- ===============================================
SELECT 'Alertaid Database Schema Created Successfully!' as status;
SELECT 'Tables Created: users, campaign, donation, my_contributions, volunteers, seek_for_help, weather_alerts' as tables;
SELECT 'Additional Features: Views, Triggers, Stored Procedures, Indexes' as features;