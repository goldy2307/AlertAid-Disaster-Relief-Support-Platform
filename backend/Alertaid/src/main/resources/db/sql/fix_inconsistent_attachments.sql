-- Fix inconsistent attachment data
-- This script corrects reports that have photoCount > 0 but attachments is NULL
-- This can happen if reports were created before attachments were properly saved

-- Option 1: Set photoCount to 0 for reports with missing attachments (recommended)
UPDATE reports 
SET photo_count = 0 
WHERE photo_count > 0 
  AND (attachments IS NULL OR attachments = '' OR TRIM(attachments) = '' OR TRIM(attachments) = 'null');

-- Option 2: If you want to keep photoCount but mark them differently, you could add a flag
-- ALTER TABLE reports ADD COLUMN attachments_missing BOOLEAN DEFAULT FALSE;
-- UPDATE reports SET attachments_missing = TRUE 
-- WHERE photo_count > 0 AND (attachments IS NULL OR attachments = '' OR TRIM(attachments) = '' OR TRIM(attachments) = 'null');

-- Verify the fix
SELECT id, photo_count, 
       CASE 
         WHEN attachments IS NULL THEN 'NULL'
         WHEN attachments = '' THEN 'EMPTY'
         WHEN TRIM(attachments) = '' THEN 'WHITESPACE'
         WHEN TRIM(attachments) = 'null' THEN 'STRING_NULL'
         ELSE 'HAS_DATA'
       END as attachment_status,
       LENGTH(attachments) as attachment_length
FROM reports 
WHERE photo_count > 0
ORDER BY id DESC
LIMIT 20;

