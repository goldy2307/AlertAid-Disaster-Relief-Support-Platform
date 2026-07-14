-- Flyway baseline schema for Postgres, aligned with current JPA entities and Reports workflow

-- Users
CREATE TABLE IF NOT EXISTS users (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(100),
  email VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL DEFAULT 'CITIZEN',
  phone VARCHAR(15),
  address VARCHAR(255),
  state VARCHAR(100),
  city VARCHAR(100),
  pincode VARCHAR(10),
  gender VARCHAR(20),
  org_name VARCHAR(200),
  org_type VARCHAR(100),
  license_number VARCHAR(100),
  services VARCHAR(255),
  support_mode VARCHAR(50),
  expertise VARCHAR(255),
  availability VARCHAR(50),
  experience_level VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at);

-- Volunteers (used by Volunteer entity)
CREATE TABLE IF NOT EXISTS volunteers (
  id BIGSERIAL PRIMARY KEY,
  full_name VARCHAR(150),
  email VARCHAR(100),
  phone VARCHAR(20),
  address VARCHAR(255),
  skills VARCHAR(255),
  availability VARCHAR(50),
  expertise VARCHAR(255),
  experience_level VARCHAR(50),
  support_mode VARCHAR(50)
);

-- Campaigns/Donations (table names match entity defaults)
CREATE TABLE IF NOT EXISTS campaign (
  id BIGSERIAL PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  goal_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
  collected_amount NUMERIC(12,2) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS donation (
  id BIGSERIAL PRIMARY KEY,
  campaign_id BIGINT REFERENCES campaign(id),
  donor_name VARCHAR(150),
  donor_email VARCHAR(150),
  amount NUMERIC(12,2) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_donation_campaign ON donation(campaign_id);

CREATE TABLE IF NOT EXISTS my_contributions (
  id BIGSERIAL PRIMARY KEY,
  contributor_name VARCHAR(255),
  email VARCHAR(255),
  contribution_type VARCHAR(255),
  details TEXT,
  amount NUMERIC(12,2),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Seek for Help (aligned to entity fields)
CREATE TABLE IF NOT EXISTS seek_for_help (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(150),
  email VARCHAR(150),
  phone VARCHAR(20),
  help_type VARCHAR(100),
  description TEXT
);

-- Weather Alerts (aligned to entity fields)
CREATE TABLE IF NOT EXISTS weather_alerts (
  id BIGSERIAL PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  severity VARCHAR(50),
  region VARCHAR(150),
  issued_at TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_weather_region ON weather_alerts(region);
CREATE INDEX IF NOT EXISTS idx_weather_issued_at ON weather_alerts(issued_at);

-- Reports workflow
CREATE TABLE IF NOT EXISTS reports (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES users(id),
  location VARCHAR(255),
  description TEXT,
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  latitude DOUBLE PRECISION,
  longitude DOUBLE PRECISION,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL
);
CREATE INDEX IF NOT EXISTS idx_reports_status ON reports(status);
CREATE INDEX IF NOT EXISTS idx_reports_created_at ON reports(created_at);
CREATE INDEX IF NOT EXISTS idx_reports_user_id ON reports(user_id);

CREATE TABLE IF NOT EXISTS admin_decisions (
  id BIGSERIAL PRIMARY KEY,
  admin_id BIGINT NOT NULL REFERENCES users(id),
  report_id BIGINT NOT NULL REFERENCES reports(id) ON DELETE CASCADE,
  decision VARCHAR(20) NOT NULL,
  decision_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  notes TEXT
);
CREATE INDEX IF NOT EXISTS idx_decisions_report_id ON admin_decisions(report_id);
CREATE INDEX IF NOT EXISTS idx_decisions_admin_id ON admin_decisions(admin_id);
CREATE INDEX IF NOT EXISTS idx_decisions_created_at ON admin_decisions(decision_timestamp);

CREATE TABLE IF NOT EXISTS logs (
  id BIGSERIAL PRIMARY KEY,
  action VARCHAR(50) NOT NULL,
  user_id BIGINT NULL REFERENCES users(id),
  message TEXT,
  level VARCHAR(20),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_logs_level ON logs(level);
CREATE INDEX IF NOT EXISTS idx_logs_created_at ON logs(created_at);
