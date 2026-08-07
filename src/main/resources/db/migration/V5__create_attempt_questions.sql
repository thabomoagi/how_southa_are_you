CREATE TABLE attempt_questions (
    id BIGSERIAL PRIMARY KEY,
    attempt_id UUID NOT NULL REFERENCES attempts(id) ON DELETE CASCADE,
    question_id BIGINT NOT NULL REFERENCES questions(id),
    position INTEGER NOT NULL,
    CONSTRAINT uq_attempt_questions_attempt_question UNIQUE (attempt_id, question_id)
);