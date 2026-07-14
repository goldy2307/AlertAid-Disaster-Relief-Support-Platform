-- Entities use `double`/`Double` for money fields; V1 created them as NUMERIC.
-- Hibernate ddl-auto=validate treats these as different types -> fix to match.
ALTER TABLE campaign
  ALTER COLUMN goal_amount TYPE DOUBLE PRECISION USING goal_amount::double precision,
  ALTER COLUMN collected_amount TYPE DOUBLE PRECISION USING collected_amount::double precision;

ALTER TABLE donation
  ALTER COLUMN amount TYPE DOUBLE PRECISION USING amount::double precision;

ALTER TABLE my_contributions
  ALTER COLUMN amount TYPE DOUBLE PRECISION USING amount::double precision;

-- Campaign entity has banking/payout fields never created in V1.
ALTER TABLE campaign
  ADD COLUMN IF NOT EXISTS account_holder_name VARCHAR(255),
  ADD COLUMN IF NOT EXISTS account_number VARCHAR(50),
  ADD COLUMN IF NOT EXISTS bank_name VARCHAR(255),
  ADD COLUMN IF NOT EXISTS ifsc_code VARCHAR(20),
  ADD COLUMN IF NOT EXISTS upi_id VARCHAR(255),
  ADD COLUMN IF NOT EXISTS qr_code_image_base64 TEXT,
  ADD COLUMN IF NOT EXISTS beneficiary_image_base64 TEXT;

-- Donation entity has payment/gateway fields never created in V1.
ALTER TABLE donation
  ADD COLUMN IF NOT EXISTS donor_phone VARCHAR(25),
  ADD COLUMN IF NOT EXISTS currency VARCHAR(10) DEFAULT 'INR',
  ADD COLUMN IF NOT EXISTS payment_method VARCHAR(20),
  ADD COLUMN IF NOT EXISTS payment_status VARCHAR(20) DEFAULT 'PENDING',
  ADD COLUMN IF NOT EXISTS gateway_order_id VARCHAR(100),
  ADD COLUMN IF NOT EXISTS gateway_payment_id VARCHAR(100),
  ADD COLUMN IF NOT EXISTS gateway_signature VARCHAR(255),
  ADD COLUMN IF NOT EXISTS payment_reference VARCHAR(100),
  ADD COLUMN IF NOT EXISTS test_mode BOOLEAN DEFAULT FALSE;
