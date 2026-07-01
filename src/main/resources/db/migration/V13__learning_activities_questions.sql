CREATE TABLE IF NOT EXISTS learning_activities (
    id UUID PRIMARY KEY,
    code VARCHAR(140) NOT NULL UNIQUE,
    title VARCHAR(240) NOT NULL,
    description TEXT,
    subject_id UUID NOT NULL,
    topic_id UUID NOT NULL,
    skill_id UUID NOT NULL,
    objective_id UUID,
    curriculum_framework_id UUID,
    curriculum_band_id UUID,
    curriculum_expectation_id UUID,
    activity_type VARCHAR(60) NOT NULL,
    estimated_duration_minutes INTEGER NOT NULL,
    complexity_level VARCHAR(40) NOT NULL,
    depth_level VARCHAR(40) NOT NULL,
    display_order INTEGER NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_learning_activities_subject
        FOREIGN KEY (subject_id) REFERENCES learning_subjects(id),

    CONSTRAINT fk_learning_activities_topic
        FOREIGN KEY (topic_id) REFERENCES learning_topics(id),

    CONSTRAINT fk_learning_activities_skill
        FOREIGN KEY (skill_id) REFERENCES learning_skills(id),

    CONSTRAINT fk_learning_activities_objective
        FOREIGN KEY (objective_id) REFERENCES learning_objectives(id),

    CONSTRAINT fk_learning_activities_curriculum_framework
        FOREIGN KEY (curriculum_framework_id) REFERENCES curriculum_frameworks(id),

    CONSTRAINT fk_learning_activities_curriculum_band
        FOREIGN KEY (curriculum_band_id) REFERENCES curriculum_bands(id),

    CONSTRAINT fk_learning_activities_curriculum_expectation
        FOREIGN KEY (curriculum_expectation_id) REFERENCES curriculum_expectations(id)
);

CREATE TABLE IF NOT EXISTS activity_questions (
    id UUID PRIMARY KEY,
    activity_id UUID NOT NULL,
    code VARCHAR(160) NOT NULL,
    prompt TEXT NOT NULL,
    explanation TEXT,
    question_type VARCHAR(60) NOT NULL,
    subject_id UUID NOT NULL,
    topic_id UUID NOT NULL,
    skill_id UUID NOT NULL,
    objective_id UUID,
    complexity_level VARCHAR(40) NOT NULL,
    depth_level VARCHAR(40) NOT NULL,
    display_order INTEGER NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_activity_questions_activity
        FOREIGN KEY (activity_id) REFERENCES learning_activities(id),

    CONSTRAINT fk_activity_questions_subject
        FOREIGN KEY (subject_id) REFERENCES learning_subjects(id),

    CONSTRAINT fk_activity_questions_topic
        FOREIGN KEY (topic_id) REFERENCES learning_topics(id),

    CONSTRAINT fk_activity_questions_skill
        FOREIGN KEY (skill_id) REFERENCES learning_skills(id),

    CONSTRAINT fk_activity_questions_objective
        FOREIGN KEY (objective_id) REFERENCES learning_objectives(id),

    CONSTRAINT uq_activity_questions_activity_code
        UNIQUE (activity_id, code)
);

CREATE TABLE IF NOT EXISTS question_options (
    id UUID PRIMARY KEY,
    question_id UUID NOT NULL,
    label VARCHAR(10) NOT NULL,
    option_text TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL,
    display_order INTEGER NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_question_options_question
        FOREIGN KEY (question_id) REFERENCES activity_questions(id),

    CONSTRAINT uq_question_options_question_label
        UNIQUE (question_id, label)
);

CREATE INDEX IF NOT EXISTS idx_learning_activities_subject_topic_skill
    ON learning_activities(subject_id, topic_id, skill_id);

CREATE INDEX IF NOT EXISTS idx_learning_activities_objective
    ON learning_activities(objective_id);

CREATE INDEX IF NOT EXISTS idx_learning_activities_curriculum
    ON learning_activities(curriculum_framework_id, curriculum_band_id, curriculum_expectation_id);

CREATE INDEX IF NOT EXISTS idx_activity_questions_activity_id
    ON activity_questions(activity_id);

CREATE INDEX IF NOT EXISTS idx_activity_questions_subject_topic_skill
    ON activity_questions(subject_id, topic_id, skill_id);

CREATE INDEX IF NOT EXISTS idx_question_options_question_id
    ON question_options(question_id);
