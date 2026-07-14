-- MySQL ALTERs to upgrade an existing Alertaid DB to match current models and registration fields
USE alertaid_db;

-- users: add columns if not exist
ALTER TABLE users
  ADD COLUMN IF NOT EXISTS address VARCHAR(255),
  ADD COLUMN IF NOT EXISTS state VARCHAR(100),
  ADD COLUMN IF NOT EXISTS city VARCHAR(100),
  ADD COLUMN IF NOT EXISTS pincode VARCHAR(10),
  ADD COLUMN IF NOT EXISTS gender VARCHAR(20),
  ADD COLUMN IF NOT EXISTS org_name VARCHAR(200),
  ADD COLUMN IF NOT EXISTS org_type VARCHAR(100),
  ADD COLUMN IF NOT EXISTS license_number VARCHAR(100),
  ADD COLUMN IF NOT EXISTS services VARCHAR(255),
  ADD COLUMN IF NOT EXISTS support_mode VARCHAR(50),
  ADD COLUMN IF NOT EXISTS expertise VARCHAR(255),
  ADD COLUMN IF NOT EXISTS availability VARCHAR(50),
  ADD COLUMN IF NOT EXISTS experience_level VARCHAR(50);

-- volunteers: ensure expanded columns
ALTER TABLE volunteers
  ADD COLUMN IF NOT EXISTS full_name VARCHAR(150),
  ADD COLUMN IF NOT EXISTS email VARCHAR(100),
  ADD COLUMN IF NOT EXISTS phone VARCHAR(20),
  ADD COLUMN IF NOT EXISTS address VARCHAR(255),
  MODIFY COLUMN skills VARCHAR(255),
  ADD COLUMN IF NOT EXISTS availability VARCHAR(50),
  ADD COLUMN IF NOT EXISTS expertise VARCHAR(255),
  ADD COLUMN IF NOT EXISTS experience_level VARCHAR(50),
  ADD COLUMN IF NOT EXISTS support_mode VARCHAR(50);

-- campaigns: align to current model names
-- If you previously had target_amount/collected_amount, keep them; current model uses goal/collected (double). No change required if existing.
ALTER TABLE campaigns
  ADD COLUMN IF NOT EXISTS account_holder_name VARCHAR(255),
  ADD COLUMN IF NOT EXISTS account_number VARCHAR(64),
  ADD COLUMN IF NOT EXISTS bank_name VARCHAR(255),
  ADD COLUMN IF NOT EXISTS ifsc_code VARCHAR(32),
  ADD COLUMN IF NOT EXISTS upi_id VARCHAR(128),
  ADD COLUMN IF NOT EXISTS qr_code_image_base64 LONGTEXT,
  ADD COLUMN IF NOT EXISTS beneficiary_image_base64 LONGTEXT;

-- Stored procedure to create campaign with media and account details
DELIMITER $$
CREATE PROCEDURE IF NOT EXISTS sp_create_campaign_with_media(
  IN p_title VARCHAR(255),
  IN p_description TEXT,
  IN p_goal DECIMAL(12,2),
  IN p_account_holder VARCHAR(255),
  IN p_account_number VARCHAR(64),
  IN p_bank_name VARCHAR(255),
  IN p_ifsc VARCHAR(32),
  IN p_upi VARCHAR(128),
  IN p_qr LONGTEXT,
  IN p_beneficiary LONGTEXT
)
BEGIN
  INSERT INTO campaigns(title, description, target_amount, collected_amount,
    account_holder_name, account_number, bank_name, ifsc_code, upi_id,
    qr_code_image_base64, beneficiary_image_base64)
  VALUES(p_title, p_description, p_goal, 0,
    p_account_holder, p_account_number, p_bank_name, p_ifsc, p_upi,
    p_qr, p_beneficiary);
  SELECT LAST_INSERT_ID() AS id;
END $$
DELIMITER ;

-- Stored procedure to list recent donations for a user (by email)
DELIMITER $$
CREATE PROCEDURE IF NOT EXISTS sp_list_donations_by_email(IN p_email VARCHAR(150))
BEGIN
  SELECT d.* FROM donations d WHERE d.donor_email = p_email ORDER BY d.created_at DESC LIMIT 100;
END $$
DELIMITER ;

-- donations: change donor columns
-- If your schema used donor_id/payment_status, you can keep them or run below to align with current model simply storing donorName/email and amount
-- NOTE: Only run if you truly want to drop old FKs/columns.
-- ALTER TABLE donations
--   DROP FOREIGN KEY fk_don_user,
--   DROP COLUMN donor_id,
--   DROP COLUMN payment_status,
--   ADD COLUMN IF NOT EXISTS donor_name VARCHAR(150),
--   ADD COLUMN IF NOT EXISTS donor_email VARCHAR(150);

-- seek_for_help: align to current model
ALTER TABLE seek_for_help
  ADD COLUMN IF NOT EXISTS name VARCHAR(150),
  ADD COLUMN IF NOT EXISTS email VARCHAR(150),
  ADD COLUMN IF NOT EXISTS phone VARCHAR(20),
  ADD COLUMN IF NOT EXISTS help_type VARCHAR(100);

-- weather_alerts: align to current model
ALTER TABLE weather_alerts
  ADD COLUMN IF NOT EXISTS description TEXT,
  ADD COLUMN IF NOT EXISTS severity VARCHAR(50),
  ADD COLUMN IF NOT EXISTS region VARCHAR(150),
  MODIFY COLUMN issued_at DATETIME;
