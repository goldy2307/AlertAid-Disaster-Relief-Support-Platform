-- PostgreSQL schema for Alertaid (based on provided design)

CREATE TABLE IF NOT EXISTS users (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(100),
  email VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL DEFAULT 'CITIZEN',
  phone VARCHAR(15),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS volunteers (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT REFERENCES users(id),
  skills TEXT,
  availability_status BOOLEAN,
  assigned_tasks TEXT
);

CREATE TABLE IF NOT EXISTS campaigns (
  id BIGSERIAL PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  target_amount NUMERIC(12,2) NOT NULL,
  collected_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
  start_date DATE,
  end_date DATE,
  created_by BIGINT REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS donations (
  id BIGSERIAL PRIMARY KEY,
  campaign_id BIGINT REFERENCES campaigns(id),
  donor_id BIGINT REFERENCES users(id),
  amount NUMERIC(12,2) NOT NULL,
  payment_status VARCHAR(20) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS my_contributions (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT REFERENCES users(id),
  campaign_id BIGINT REFERENCES campaigns(id),
  amount NUMERIC(12,2)
);

CREATE TABLE IF NOT EXISTS seek_for_help (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT REFERENCES users(id),
  description TEXT,
  location VARCHAR(255),
  status VARCHAR(20) DEFAULT 'OPEN',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS weather_alerts (
  id BIGSERIAL PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  message TEXT,
  alert_level VARCHAR(20) NOT NULL,
  issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  created_by BIGINT REFERENCES users(id)
);
