ALTER TABLE attempts
ADD COLUMN expires_at TIMESTAMP;

UPDATE attempts
SET expires_at = started_at + INTERVAL '120 seconds';

ALTER TABLE attempts
ALTER COLUMN expires_at SET NOT NULL;