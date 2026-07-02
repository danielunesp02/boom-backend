CREATE TABLE IF NOT EXISTS student_learning_events (
    id UUID PRIMARY KEY,

    student_id UUID NOT NULL,
    guardian_id UUID,

    assessment_attempt_id UUID,
    answer_submission_id UUID,

    subject_id UUID,
    topic_id UUID,
    skill_id UUID,
    learning_objective_id UUID,

    activity_id UUID,
    question_id UUID,

    curriculum_framework_id UUID,
    curriculum_band_id UUID,
    curriculum_expectation_id UUID,

    event_type VARCHAR(80) NOT NULL,

    event_time TIMESTAMP NOT NULL,
    event_date DATE NOT NULL,
    event_year INTEGER NOT NULL,
    event_month INTEGER NOT NULL,
    event_day INTEGER NOT NULL,
    event_week INTEGER NOT NULL,

    client_event_time TIMESTAMP,
    server_received_at TIMESTAMP NOT NULL,

    country_code VARCHAR(2),
    grade_level VARCHAR(40),
    target_school_system VARCHAR(80),
    age_at_event_months INTEGER,

    knowledge_level_before VARCHAR(40),
    knowledge_level_after VARCHAR(40),
    complexity_level VARCHAR(40),
    depth_level VARCHAR(40),

    is_correct BOOLEAN,
    score NUMERIC(8,4),
    time_spent_seconds INTEGER,
    attempt_number INTEGER,

    source_channel VARCHAR(40) NOT NULL,
    locale VARCHAR(10),

    metadata JSONB,

    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_student_learning_events_student
        FOREIGN KEY (student_id) REFERENCES students(id),

    CONSTRAINT fk_student_learning_events_attempt
        FOREIGN KEY (assessment_attempt_id) REFERENCES assessment_attempts(id),

    CONSTRAINT fk_student_learning_events_answer
        FOREIGN KEY (answer_submission_id) REFERENCES answer_submissions(id),

    CONSTRAINT fk_student_learning_events_subject
        FOREIGN KEY (subject_id) REFERENCES learning_subjects(id),

    CONSTRAINT fk_student_learning_events_topic
        FOREIGN KEY (topic_id) REFERENCES learning_topics(id),

    CONSTRAINT fk_student_learning_events_skill
        FOREIGN KEY (skill_id) REFERENCES learning_skills(id),

    CONSTRAINT fk_student_learning_events_objective
        FOREIGN KEY (learning_objective_id) REFERENCES learning_objectives(id),

    CONSTRAINT fk_student_learning_events_activity
        FOREIGN KEY (activity_id) REFERENCES learning_activities(id),

    CONSTRAINT fk_student_learning_events_question
        FOREIGN KEY (question_id) REFERENCES activity_questions(id),

    CONSTRAINT fk_student_learning_events_curriculum_framework
        FOREIGN KEY (curriculum_framework_id) REFERENCES curriculum_frameworks(id),

    CONSTRAINT fk_student_learning_events_curriculum_band
        FOREIGN KEY (curriculum_band_id) REFERENCES curriculum_bands(id),

    CONSTRAINT fk_student_learning_events_curriculum_expectation
        FOREIGN KEY (curriculum_expectation_id) REFERENCES curriculum_expectations(id)
);

CREATE INDEX IF NOT EXISTS idx_student_learning_events_student_time
    ON student_learning_events (student_id, event_time);

CREATE INDEX IF NOT EXISTS idx_student_learning_events_student_skill_date
    ON student_learning_events (student_id, skill_id, event_date);

CREATE INDEX IF NOT EXISTS idx_student_learning_events_activity_date
    ON student_learning_events (activity_id, event_date);

CREATE INDEX IF NOT EXISTS idx_student_learning_events_attempt
    ON student_learning_events (assessment_attempt_id);

CREATE INDEX IF NOT EXISTS idx_student_learning_events_event_type_date
    ON student_learning_events (event_type, event_date);

CREATE INDEX IF NOT EXISTS idx_student_learning_events_subject_topic_skill
    ON student_learning_events (subject_id, topic_id, skill_id);

CREATE INDEX IF NOT EXISTS idx_student_learning_events_curriculum_date
    ON student_learning_events (curriculum_framework_id, curriculum_band_id, curriculum_expectation_id, event_date);
