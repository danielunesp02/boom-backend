CREATE TABLE IF NOT EXISTS student_subject_learning_strategy (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL,
    subject_id UUID NOT NULL,
    methodology_code VARCHAR(64) NOT NULL DEFAULT 'BALANCED',
    initial_assessment_score NUMERIC(5,2),
    current_mastery_score NUMERIC(5,2) NOT NULL DEFAULT 0,
    expected_mastery_score NUMERIC(5,2) NOT NULL DEFAULT 0,
    high_performance_target_score NUMERIC(5,2) NOT NULL DEFAULT 0,
    calibration_status VARCHAR(32) NOT NULL DEFAULT 'NOT_CALIBRATED',
    weekly_study_minutes_goal INTEGER NOT NULL DEFAULT 120,
    curve_start_date DATE NOT NULL DEFAULT CURRENT_DATE,
    target_date DATE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_student_subject_strategy_subject
        FOREIGN KEY (subject_id) REFERENCES learning_subjects(id),
    CONSTRAINT uq_student_subject_strategy
        UNIQUE (student_id, subject_id)
);

CREATE INDEX IF NOT EXISTS idx_student_subject_strategy_student
    ON student_subject_learning_strategy(student_id);

CREATE INDEX IF NOT EXISTS idx_student_subject_strategy_subject
    ON student_subject_learning_strategy(subject_id);
