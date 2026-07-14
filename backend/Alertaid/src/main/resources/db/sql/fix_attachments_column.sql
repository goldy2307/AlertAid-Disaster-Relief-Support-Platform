-- Manual SQL script to fix the attachments column size issue
-- Run this directly on your MySQL database to fix the "Data too long for column 'attachments'" error

-- For MySQL
ALTER TABLE reports MODIFY COLUMN attachments LONGTEXT;

-- Verify the change
-- DESCRIBE reports;

