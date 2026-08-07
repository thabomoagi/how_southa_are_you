ALTER TABLE questions
ADD COLUMN external_id VARCHAR(50) UNIQUE,
ADD COLUMN era VARCHAR(20),
ADD COLUMN explanation TEXT;

CREATE TABLE thirty_seconds_cards (
    id BIGSERIAL PRIMARY KEY,
    external_id VARCHAR(50) UNIQUE NOT NULL,
    category VARCHAR(100) NOT NULL,
    difficulty VARCHAR(20) NOT NULL,
    words TEXT NOT NULL
);