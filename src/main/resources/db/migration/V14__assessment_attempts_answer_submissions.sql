CREATE TABLE IF NOT EXISTS assessment_attempts (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL,
    guardian_id UUID NOT NULL,
    activity_id UUID NOT NULL,
    status VARCHAR(40) NOT NULL,
    source_channel VARCHAR(40) NOT NULL,
    locale VARCHAR(10),
    total_questions INTEGER NOT NULL DEFAULT 0,
    answered_questions INTEGER NOT NULL DEFAULT 0,
    correct_answers INTEGER NOT NULL DEFAULT 0,
    score NUMERIC(8,4),
    accuracy NUMERIC(8,4),
    started_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_assessment_attempts_student
        FOREIGN KEY (student_id) REFERENCES students(id),

    CONSTRAINT fk_assessment_attempts_activity
        FOREIGN KEY (activity_id) REFERENCES learning_activities(id)
);

CREATE TABLE IF NOT EXISTS answer_submissions (
    id UUID PRIMARY KEY,
    attempt_id UUID NOT NULL,
    question_id UUID NOT NULL,
    selected_option_id UUID,
    text_answer TEXT,
    correct BOOLEAN NOT NULL,
    score NUMERIC(8,4) NOT NULL,
    time_spent_seconds INTEGER,
    submitted_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_answer_submissions_attempt
        FOREIGN KEY (attempt_id) REFERENCES assessment_attempts(id),

    CONSTRAINT fk_answer_submissions_question
        FOREIGN KEY (question_id) REFERENCES activity_questions(id),

    CONSTRAINT fk_answer_submissions_selected_option
        FOREIGN KEY (selected_option_id) REFERENCES question_options(id),

    CONSTRAINT uq_answer_submissions_attempt_question
        UNIQUE (attempt_id, question_id)
);

CREATE INDEX IF NOT EXISTS idx_assessment_attempts_student_started
    ON assessment_attempts(student_id, started_at);

CREATE INDEX IF NOT EXISTS idx_assessment_attempts_guardian_started
    ON assessment_attempts(guardian_id, started_at);

CREATE INDEX IF NOT EXISTS idx_assessment_attempts_activity
    ON assessment_attempts(activity_id);

CREATE INDEX IF NOT EXISTS idx_answer_submissions_attempt
    ON answer_submissions(attempt_id);

CREATE INDEX IF NOT EXISTS idx_answer_submissions_question
    ON answer_submissions(question_id);
