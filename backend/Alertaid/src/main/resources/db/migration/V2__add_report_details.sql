-- Migration to add detailed fields to reports table for enhanced disaster reporting
-- This migration adds all the fields from the report form to the database schema

-- Add new columns to reports table
ALTER TABLE reports 
ADD COLUMN severity VARCHAR(20) NOT NULL DEFAULT 'low',
ADD COLUMN disaster_type VARCHAR(100) NOT NULL DEFAULT 'other',
ADD COLUMN people_affected VARCHAR(50),
ADD COLUMN injuries VARCHAR(50),
ADD COLUMN reporter_name VARCHAR(255) NOT NULL DEFAULT 'Unknown',
ADD COLUMN reporter_phone VARCHAR(20) NOT NULL DEFAULT 'Unknown',
ADD COLUMN additional_info TEXT,
ADD COLUMN photo_count INTEGER DEFAULT 0;

-- Add indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_reports_severity ON reports(severity);
CREATE INDEX IF NOT EXISTS idx_reports_disaster_type ON reports(disaster_type);
CREATE INDEX IF NOT EXISTS idx_reports_reporter_phone ON reports(reporter_phone);

-- Update existing reports with default values (if any exist)
UPDATE reports 
SET 
    severity = 'medium',
    disaster_type = 'other',
    reporter_name = COALESCE(reporter_name, 'Unknown'),
    reporter_phone = COALESCE(reporter_phone, 'Unknown')
WHERE severity IS NULL OR disaster_type IS NULL;