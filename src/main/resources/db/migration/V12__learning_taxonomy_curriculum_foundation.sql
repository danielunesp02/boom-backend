CREATE TABLE IF NOT EXISTS learning_subjects (
    id UUID PRIMARY KEY,
    code VARCHAR(80) NOT NULL UNIQUE,
    default_name VARCHAR(180) NOT NULL,
    description TEXT,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS learning_topics (
    id UUID PRIMARY KEY,
    subject_id UUID NOT NULL,
    code VARCHAR(100) NOT NULL,
    default_name VARCHAR(180) NOT NULL,
    description TEXT,
    display_order INTEGER NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_learning_topics_subject FOREIGN KEY (subject_id) REFERENCES learning_subjects(id),
    CONSTRAINT uq_learning_topics_subject_code UNIQUE (subject_id, code)
);

CREATE TABLE IF NOT EXISTS learning_skills (
    id UUID PRIMARY KEY,
    topic_id UUID NOT NULL,
    code VARCHAR(120) NOT NULL,
    default_name VARCHAR(220) NOT NULL,
    description TEXT,
    display_order INTEGER NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_learning_skills_topic FOREIGN KEY (topic_id) REFERENCES learning_topics(id),
    CONSTRAINT uq_learning_skills_topic_code UNIQUE (topic_id, code)
);

CREATE TABLE IF NOT EXISTS learning_objectives (
    id UUID PRIMARY KEY,
    skill_id UUID NOT NULL,
    code VARCHAR(140) NOT NULL,
    description TEXT NOT NULL,
    complexity_level VARCHAR(40) NOT NULL,
    depth_level VARCHAR(40) NOT NULL,
    display_order INTEGER NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_learning_objectives_skill FOREIGN KEY (skill_id) REFERENCES learning_skills(id),
    CONSTRAINT uq_learning_objectives_skill_code UNIQUE (skill_id, code)
);

CREATE TABLE IF NOT EXISTS curriculum_frameworks (
    id UUID PRIMARY KEY,
    country_code VARCHAR(2) NOT NULL,
    code VARCHAR(100) NOT NULL,
    name VARCHAR(220) NOT NULL,
    version VARCHAR(80) NOT NULL,
    source_type VARCHAR(60) NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_curriculum_framework_country_code UNIQUE (country_code, code, version)
);

CREATE TABLE IF NOT EXISTS curriculum_bands (
    id UUID PRIMARY KEY,
    framework_id UUID NOT NULL,
    code VARCHAR(100) NOT NULL,
    min_age_months INTEGER NOT NULL,
    max_age_months INTEGER NOT NULL,
    grade_level VARCHAR(40) NOT NULL,
    school_stage VARCHAR(80) NOT NULL,
    display_order INTEGER NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_curriculum_bands_framework FOREIGN KEY (framework_id) REFERENCES curriculum_frameworks(id),
    CONSTRAINT uq_curriculum_bands_framework_code UNIQUE (framework_id, code)
);

CREATE TABLE IF NOT EXISTS curriculum_expectations (
    id UUID PRIMARY KEY,
    band_id UUID NOT NULL,
    subject_id UUID NOT NULL,
    topic_id UUID NOT NULL,
    skill_id UUID NOT NULL,
    objective_id UUID,
    expected_knowledge_level VARCHAR(40) NOT NULL,
    expected_complexity_level VARCHAR(40) NOT NULL,
    expected_depth_level VARCHAR(40) NOT NULL,
    priority VARCHAR(40) NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_curriculum_expectations_band FOREIGN KEY (band_id) REFERENCES curriculum_bands(id),
    CONSTRAINT fk_curriculum_expectations_subject FOREIGN KEY (subject_id) REFERENCES learning_subjects(id),
    CONSTRAINT fk_curriculum_expectations_topic FOREIGN KEY (topic_id) REFERENCES learning_topics(id),
    CONSTRAINT fk_curriculum_expectations_skill FOREIGN KEY (skill_id) REFERENCES learning_skills(id),
    CONSTRAINT fk_curriculum_expectations_objective FOREIGN KEY (objective_id) REFERENCES learning_objectives(id),
    CONSTRAINT uq_curriculum_expectation_band_objective UNIQUE (band_id, objective_id)
);

CREATE INDEX IF NOT EXISTS idx_learning_topics_subject_id ON learning_topics(subject_id);
CREATE INDEX IF NOT EXISTS idx_learning_skills_topic_id ON learning_skills(topic_id);
CREATE INDEX IF NOT EXISTS idx_learning_objectives_skill_id ON learning_objectives(skill_id);
CREATE INDEX IF NOT EXISTS idx_curriculum_frameworks_country_code ON curriculum_frameworks(country_code);
CREATE INDEX IF NOT EXISTS idx_curriculum_bands_framework_id ON curriculum_bands(framework_id);
CREATE INDEX IF NOT EXISTS idx_curriculum_bands_age_grade ON curriculum_bands(min_age_months, max_age_months, grade_level);
CREATE INDEX IF NOT EXISTS idx_curriculum_expectations_band_id ON curriculum_expectations(band_id);
CREATE INDEX IF NOT EXISTS idx_curriculum_expectations_skill_id ON curriculum_expectations(skill_id);
CREATE INDEX IF NOT EXISTS idx_curriculum_expectations_subject_topic_skill ON curriculum_expectations(subject_id, topic_id, skill_id);
