CREATE TABLE IF NOT EXISTS students (
    id UUID PRIMARY KEY,
    display_name VARCHAR(160) NOT NULL,
    birth_date DATE,
    grade_level VARCHAR(40) NOT NULL,
    target_school_system VARCHAR(40) NOT NULL,
    preferred_locale VARCHAR(10) NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS guardian_student_relationships (
    id UUID PRIMARY KEY,
    guardian_id UUID NOT NULL,
    student_id UUID NOT NULL,
    relationship_type VARCHAR(40) NOT NULL,
    is_primary BOOLEAN NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_guardian_student_relationship_guardian
        FOREIGN KEY (guardian_id) REFERENCES guardian_account(id),

    CONSTRAINT fk_guardian_student_relationship_student
        FOREIGN KEY (student_id) REFERENCES students(id)
);

CREATE INDEX IF NOT EXISTS idx_guardian_student_relationships_guardian_id
    ON guardian_student_relationships (guardian_id);

CREATE INDEX IF NOT EXISTS idx_guardian_student_relationships_student_id
    ON guardian_student_relationships (student_id);

CREATE UNIQUE INDEX IF NOT EXISTS uq_guardian_student_active
    ON guardian_student_relationships (guardian_id, student_id)
    WHERE status = 'ACTIVE';

CREATE UNIQUE INDEX IF NOT EXISTS uq_guardian_student_primary_active
    ON guardian_student_relationships (guardian_id)
    WHERE status = 'ACTIVE' AND is_primary = true;
