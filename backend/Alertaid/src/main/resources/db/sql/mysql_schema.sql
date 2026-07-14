-- MySQL schema for Alertaid (complete, aligned with frontend fields)

CREATE TABLE IF NOT EXISTS users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100),
  email VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  role ENUM('CITIZEN','VOLUNTEER','ORG','ADMIN') NOT NULL DEFAULT 'CITIZEN',
  phone VARCHAR(15),
  address VARCHAR(255),
  state VARCHAR(100),
  city VARCHAR(100),
  pincode VARCHAR(10),
  gender VARCHAR(20),
  -- Org fields
  org_name VARCHAR(200),
  org_type VARCHAR(100),
  license_number VARCHAR(100),
  services VARCHAR(255),
  support_mode VARCHAR(50),
  -- Volunteer fields
  expertise VARCHAR(255),
  availability VARCHAR(50),
  experience_level VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS volunteers (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT,
  full_name VARCHAR(150),
  email VARCHAR(100),
  phone VARCHAR(20),
  address VARCHAR(255),
  skills VARCHAR(255),
  availability VARCHAR(50),
  expertise VARCHAR(255),
  experience_level VARCHAR(50),
  support_mode VARCHAR(50),
  CONSTRAINT fk_vol_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS campaigns (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  target_amount DECIMAL(12,2) NOT NULL,
  collected_amount DECIMAL(12,2) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS donations (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  campaign_id BIGINT,
  donor_name VARCHAR(150),
  donor_email VARCHAR(150),
  amount DECIMAL(12,2) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_don_camp FOREIGN KEY (campaign_id) REFERENCES campaigns(id)
);

CREATE TABLE IF NOT EXISTS my_contributions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  contributor_name VARCHAR(150),
  email VARCHAR(150),
  contribution_type VARCHAR(100),
  details VARCHAR(255),
  amount DECIMAL(12,2)
);

CREATE TABLE IF NOT EXISTS seek_for_help (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(150),
  email VARCHAR(150),
  phone VARCHAR(20),
  help_type VARCHAR(100),
  description TEXT
);

CREATE TABLE IF NOT EXISTS weather_alerts (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  severity VARCHAR(50),
  region VARCHAR(150),
  issued_at DATETIME
);
