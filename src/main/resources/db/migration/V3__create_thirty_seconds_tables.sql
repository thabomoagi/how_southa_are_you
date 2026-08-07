CREATE TABLE thirty_seconds_games (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    mode VARCHAR(20) NOT NULL,
    player_count INTEGER NOT NULL,
    total_score INTEGER,
    started_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP
);

CREATE TABLE thirty_seconds_rounds (
    id BIGSERIAL PRIMARY KEY,
    game_id UUID REFERENCES thirty_seconds_games(id) ON DELETE CASCADE,
    round_number INTEGER NOT NULL,
    prompt TEXT NOT NULL,
    score INTEGER
);