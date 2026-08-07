CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE questions (
    id BIGSERIAL PRIMARY KEY,
    category_id BIGINT REFERENCES categories(id),
    prompt TEXT NOT NULL,
    difficulty VARCHAR(20) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE options (
    id BIGSERIAL PRIMARY KEY,
    question_id BIGINT REFERENCES questions(id) ON DELETE CASCADE,
    option_text TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE attempts (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    started_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    score INTEGER,
    total_questions INTEGER,
    correct_count INTEGER
);

CREATE TABLE attempt_answers (
    id BIGSERIAL PRIMARY KEY,
    attempt_id UUID REFERENCES attempts(id) ON DELETE CASCADE,
    question_id BIGINT REFERENCES questions(id),
    selected_option_id BIGINT REFERENCES options(id),
    is_correct BOOLEAN NOT NULL,
    time_taken_ms INTEGER,
    points_earned INTEGER
);