CREATE TABLE IF NOT EXISTS learning_methodologies (
    id UUID PRIMARY KEY,
    code VARCHAR(80) NOT NULL UNIQUE,
    name VARCHAR(160) NOT NULL,
    description TEXT,
    config_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS student_learning_strategy (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL,
    methodology_id UUID NOT NULL,
    mode VARCHAR(60) NOT NULL,
    custom_config_json JSONB,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by_guardian_id UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_student_learning_strategy_student
        FOREIGN KEY (student_id) REFERENCES students(id),

    CONSTRAINT fk_student_learning_strategy_methodology
        FOREIGN KEY (methodology_id) REFERENCES learning_methodologies(id),

    CONSTRAINT fk_student_learning_strategy_guardian
        FOREIGN KEY (created_by_guardian_id) REFERENCES guardian_account(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_student_learning_strategy_active
    ON student_learning_strategy(student_id)
    WHERE active = TRUE;

CREATE TABLE IF NOT EXISTS student_skill_mastery_state (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL,
    subject_id UUID NOT NULL,
    topic_id UUID NOT NULL,
    skill_id UUID NOT NULL,
    mastery_status VARCHAR(60) NOT NULL,
    mastery_score NUMERIC(8,4) NOT NULL DEFAULT 0,
    accuracy NUMERIC(8,4) NOT NULL DEFAULT 0,
    questions_answered INTEGER NOT NULL DEFAULT 0,
    correct_answers INTEGER NOT NULL DEFAULT 0,
    incorrect_answers INTEGER NOT NULL DEFAULT 0,
    consecutive_successes INTEGER NOT NULL DEFAULT 0,
    consecutive_failures INTEGER NOT NULL DEFAULT 0,
    successful_reviews INTEGER NOT NULL DEFAULT 0,
    failed_reviews INTEGER NOT NULL DEFAULT 0,
    review_step_index INTEGER NOT NULL DEFAULT 0,
    review_interval_days INTEGER,
    last_practiced_at TIMESTAMP,
    next_review_at TIMESTAMP,
    requires_reassessment BOOLEAN NOT NULL DEFAULT FALSE,
    confidence_score NUMERIC(8,4) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_student_skill_mastery_state_student
        FOREIGN KEY (student_id) REFERENCES students(id),

    CONSTRAINT fk_student_skill_mastery_state_subject
        FOREIGN KEY (subject_id) REFERENCES learning_subjects(id),

    CONSTRAINT fk_student_skill_mastery_state_topic
        FOREIGN KEY (topic_id) REFERENCES learning_topics(id),

    CONSTRAINT fk_student_skill_mastery_state_skill
        FOREIGN KEY (skill_id) REFERENCES learning_skills(id),

    CONSTRAINT uq_student_skill_mastery_state_student_skill
        UNIQUE (student_id, skill_id)
);

CREATE TABLE IF NOT EXISTS student_review_queue (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL,
    subject_id UUID NOT NULL,
    topic_id UUID NOT NULL,
    skill_id UUID NOT NULL,
    source_attempt_id UUID,
    source_event_id UUID,
    reason VARCHAR(80) NOT NULL,
    priority VARCHAR(40) NOT NULL,
    scheduled_for DATE NOT NULL,
    status VARCHAR(60) NOT NULL,
    review_type VARCHAR(80) NOT NULL,
    methodology_code VARCHAR(80) NOT NULL,
    overdue_days INTEGER NOT NULL DEFAULT 0,
    requires_reassessment BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_student_review_queue_student
        FOREIGN KEY (student_id) REFERENCES students(id),

    CONSTRAINT fk_student_review_queue_subject
        FOREIGN KEY (subject_id) REFERENCES learning_subjects(id),

    CONSTRAINT fk_student_review_queue_topic
        FOREIGN KEY (topic_id) REFERENCES learning_topics(id),

    CONSTRAINT fk_student_review_queue_skill
        FOREIGN KEY (skill_id) REFERENCES learning_skills(id),

    CONSTRAINT fk_student_review_queue_attempt
        FOREIGN KEY (source_attempt_id) REFERENCES assessment_attempts(id),

    CONSTRAINT fk_student_review_queue_event
        FOREIGN KEY (source_event_id) REFERENCES student_learning_events(id)
);

CREATE TABLE IF NOT EXISTS learning_skill_relationships (
    id UUID PRIMARY KEY,
    source_skill_id UUID NOT NULL,
    target_skill_id UUID NOT NULL,
    relationship_type VARCHAR(80) NOT NULL,
    weight NUMERIC(8,4) NOT NULL DEFAULT 1,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_learning_skill_relationships_source
        FOREIGN KEY (source_skill_id) REFERENCES learning_skills(id),

    CONSTRAINT fk_learning_skill_relationships_target
        FOREIGN KEY (target_skill_id) REFERENCES learning_skills(id),

    CONSTRAINT uq_learning_skill_relationships_pair_type
        UNIQUE (source_skill_id, target_skill_id, relationship_type)
);

CREATE TABLE IF NOT EXISTS student_learning_interventions (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL,
    subject_id UUID,
    topic_id UUID,
    skill_id UUID,
    review_queue_id UUID,
    methodology_id UUID,
    intervention_type VARCHAR(80) NOT NULL,
    trigger_reason VARCHAR(120) NOT NULL,
    input_snapshot_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    recommendation_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    ai_used BOOLEAN NOT NULL DEFAULT FALSE,
    ai_provider VARCHAR(80),
    ai_model VARCHAR(120),
    status VARCHAR(60) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_student_learning_interventions_student
        FOREIGN KEY (student_id) REFERENCES students(id),

    CONSTRAINT fk_student_learning_interventions_subject
        FOREIGN KEY (subject_id) REFERENCES learning_subjects(id),

    CONSTRAINT fk_student_learning_interventions_topic
        FOREIGN KEY (topic_id) REFERENCES learning_topics(id),

    CONSTRAINT fk_student_learning_interventions_skill
        FOREIGN KEY (skill_id) REFERENCES learning_skills(id),

    CONSTRAINT fk_student_learning_interventions_review_queue
        FOREIGN KEY (review_queue_id) REFERENCES student_review_queue(id),

    CONSTRAINT fk_student_learning_interventions_methodology
        FOREIGN KEY (methodology_id) REFERENCES learning_methodologies(id)
);

CREATE INDEX IF NOT EXISTS idx_learning_methodologies_code_active
    ON learning_methodologies(code, active);

CREATE INDEX IF NOT EXISTS idx_student_learning_strategy_student_active
    ON student_learning_strategy(student_id, active);

CREATE INDEX IF NOT EXISTS idx_student_skill_mastery_state_student_status
    ON student_skill_mastery_state(student_id, mastery_status);

CREATE INDEX IF NOT EXISTS idx_student_skill_mastery_state_student_next_review
    ON student_skill_mastery_state(student_id, next_review_at);

CREATE INDEX IF NOT EXISTS idx_student_review_queue_student_status_schedule
    ON student_review_queue(student_id, status, scheduled_for);

CREATE INDEX IF NOT EXISTS idx_student_review_queue_student_priority
    ON student_review_queue(student_id, priority, scheduled_for);

CREATE INDEX IF NOT EXISTS idx_learning_skill_relationships_source_active
    ON learning_skill_relationships(source_skill_id, active);

CREATE INDEX IF NOT EXISTS idx_learning_skill_relationships_target_active
    ON learning_skill_relationships(target_skill_id, active);

CREATE INDEX IF NOT EXISTS idx_student_learning_interventions_student_created
    ON student_learning_interventions(student_id, created_at DESC);

INSERT INTO learning_methodologies (
    id,
    code,
    name,
    description,
    config_json,
    active,
    created_at,
    updated_at
)
VALUES
(
    '10000000-0000-0000-0000-000000000001',
    'LIGHT_REVIEW',
    'Light Review',
    'Low-pressure review mode for maintenance and students with stable performance.',
    '{
      "reviewIntervalsDays": [3, 7, 14, 30],
      "masteryThreshold": 75,
      "requiredSuccessfulReviews": 2,
      "maxOverdueDaysBeforeReassessment": 10,
      "moderateInactivityDays": 10,
      "abandonmentDays": 21,
      "longAbsenceDays": 45,
      "sameDayReviewEnabled": false,
      "interleavingEnabled": true,
      "correlatedTopicsEnabled": true,
      "retrievalPracticeEnabled": true,
      "workedExamplesEnabled": false
    }'::jsonb,
    TRUE,
    NOW(),
    NOW()
),
(
    '10000000-0000-0000-0000-000000000002',
    'BALANCED',
    'Balanced',
    'Default Boom methodology balancing mastery, spaced retrieval, interleaving and remediation.',
    '{
      "reviewIntervalsDays": [1, 3, 7, 14, 30],
      "masteryThreshold": 80,
      "requiredSuccessfulReviews": 2,
      "maxOverdueDaysBeforeReassessment": 7,
      "moderateInactivityDays": 7,
      "abandonmentDays": 14,
      "longAbsenceDays": 30,
      "sameDayReviewEnabled": false,
      "interleavingEnabled": true,
      "correlatedTopicsEnabled": true,
      "retrievalPracticeEnabled": true,
      "workedExamplesEnabled": true
    }'::jsonb,
    TRUE,
    NOW(),
    NOW()
),
(
    '10000000-0000-0000-0000-000000000003',
    'INTENSIVE_REMEDIATION',
    'Intensive Remediation',
    'Focused remediation mode for active gaps, low accuracy and important prerequisite skills.',
    '{
      "reviewIntervalsDays": [0, 1, 2, 4, 7, 14],
      "masteryThreshold": 85,
      "requiredSuccessfulReviews": 3,
      "maxOverdueDaysBeforeReassessment": 5,
      "moderateInactivityDays": 5,
      "abandonmentDays": 10,
      "longAbsenceDays": 21,
      "sameDayReviewEnabled": true,
      "interleavingEnabled": true,
      "correlatedTopicsEnabled": true,
      "retrievalPracticeEnabled": true,
      "workedExamplesEnabled": true
    }'::jsonb,
    TRUE,
    NOW(),
    NOW()
),
(
    '10000000-0000-0000-0000-000000000004',
    'EXAM_PREP',
    'Exam Prep',
    'Assessment-oriented mode using frequent retrieval, mixed practice and mastery checks.',
    '{
      "reviewIntervalsDays": [0, 1, 3, 7],
      "masteryThreshold": 80,
      "requiredSuccessfulReviews": 2,
      "maxOverdueDaysBeforeReassessment": 4,
      "moderateInactivityDays": 4,
      "abandonmentDays": 7,
      "longAbsenceDays": 14,
      "sameDayReviewEnabled": true,
      "interleavingEnabled": true,
      "correlatedTopicsEnabled": true,
      "retrievalPracticeEnabled": true,
      "workedExamplesEnabled": true
    }'::jsonb,
    TRUE,
    NOW(),
    NOW()
)
ON CONFLICT (code) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    config_json = EXCLUDED.config_json,
    active = EXCLUDED.active,
    updated_at = NOW();
