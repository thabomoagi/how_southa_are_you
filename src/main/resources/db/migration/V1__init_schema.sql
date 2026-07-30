CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE qna_questions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    text VARCHAR(500) NOT NULL,
    category VARCHAR(50),
    active BOOLEAN NOT NULL DEFAULT true
);

CREATE TABLE qna_options (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    question_id UUID NOT NULL REFERENCES qna_questions(id) ON DELETE CASCADE,
    text VARCHAR(255) NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE qna_attempts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    score INT NOT NULL DEFAULT 0,
    started_at TIMESTAMP NOT NULL DEFAULT now(),
    completed_at TIMESTAMP
);

CREATE TABLE qna_attempt_answers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    attempt_id UUID NOT NULL REFERENCES qna_attempts(id) ON DELETE CASCADE,
    question_id UUID NOT NULL REFERENCES qna_questions(id),
    selected_option_id UUID REFERENCES qna_options(id),
    correct BOOLEAN NOT NULL DEFAULT false,
    time_taken_ms INT
);

CREATE INDEX idx_qna_options_question_id ON qna_options(question_id);
CREATE INDEX idx_qna_attempts_user_id ON qna_attempts(user_id);
CREATE INDEX idx_qna_attempt_answers_attempt_id ON qna_attempt_answers(attempt_id);