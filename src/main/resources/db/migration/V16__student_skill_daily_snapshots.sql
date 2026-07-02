CREATE TABLE IF NOT EXISTS student_skill_daily_snapshots (
    id UUID PRIMARY KEY,
    snapshot_date DATE NOT NULL,
    student_id UUID NOT NULL,
    guardian_id UUID,
    subject_id UUID NOT NULL,
    topic_id UUID NOT NULL,
    skill_id UUID NOT NULL,
    grade_level VARCHAR(40),
    target_school_system VARCHAR(80),
    country_code VARCHAR(2),
    locale VARCHAR(10),
    questions_answered INTEGER NOT NULL DEFAULT 0,
    correct_answers INTEGER NOT NULL DEFAULT 0,
    incorrect_answers INTEGER NOT NULL DEFAULT 0,
    accuracy NUMERIC(8,4) NOT NULL DEFAULT 0,
    total_score NUMERIC(8,4) NOT NULL DEFAULT 0,
    average_score NUMERIC(8,4) NOT NULL DEFAULT 0,
    total_time_spent_seconds INTEGER NOT NULL DEFAULT 0,
    average_time_spent_seconds NUMERIC(8,2) NOT NULL DEFAULT 0,
    activities_started INTEGER NOT NULL DEFAULT 0,
    activities_completed INTEGER NOT NULL DEFAULT 0,
    attempts_completed INTEGER NOT NULL DEFAULT 0,
    first_event_at TIMESTAMP,
    last_event_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_ssds_student FOREIGN KEY (student_id) REFERENCES students(id),
    CONSTRAINT fk_ssds_subject FOREIGN KEY (subject_id) REFERENCES learning_subjects(id),
    CONSTRAINT fk_ssds_topic FOREIGN KEY (topic_id) REFERENCES learning_topics(id),
    CONSTRAINT fk_ssds_skill FOREIGN KEY (skill_id) REFERENCES learning_skills(id),
    CONSTRAINT uq_student_skill_daily_snapshot UNIQUE (snapshot_date, student_id, skill_id)
);

CREATE INDEX IF NOT EXISTS idx_ssds_student_date ON student_skill_daily_snapshots (student_id, snapshot_date);
CREATE INDEX IF NOT EXISTS idx_ssds_skill_date ON student_skill_daily_snapshots (skill_id, snapshot_date);
CREATE INDEX IF NOT EXISTS idx_ssds_guardian_date ON student_skill_daily_snapshots (guardian_id, snapshot_date);
