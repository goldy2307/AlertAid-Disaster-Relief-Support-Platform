-- attachments column was never created in V1/V2; add it (idempotent).
-- NOTE: content must match what's already recorded in Flyway history on the
-- live DB (already fixed there in a prior deploy) to avoid a checksum mismatch.
ALTER TABLE reports ADD COLUMN IF NOT EXISTS attachments TEXT;
