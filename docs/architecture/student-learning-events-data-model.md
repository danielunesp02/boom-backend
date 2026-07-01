# Student Learning Events Data Model

## Purpose

`student_learning_events` is Boom's append-only analytical event table.

It supports:

```text
student progress
skill mastery analysis
learning gap detection
age/country/curriculum benchmarks
daily/monthly/yearly snapshots
future data lake export
AI-generated parent summaries
```

## Table draft

```sql
CREATE TABLE student_learning_events (
    id UUID PRIMARY KEY,

    student_id UUID NOT NULL,
    guardian_id UUID,

    learning_session_id UUID,
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
    event_week INTEGER,

    client_event_time TIMESTAMP,
    server_received_at TIMESTAMP NOT NULL,

    country_code VARCHAR(2),
    grade_level VARCHAR(40),
    school_stage VARCHAR(80),
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

    created_at TIMESTAMP NOT NULL
);
```

## Required indexes

```sql
CREATE INDEX idx_student_learning_events_student_time
    ON student_learning_events (student_id, event_time);

CREATE INDEX idx_student_learning_events_student_skill_date
    ON student_learning_events (student_id, skill_id, event_date);

CREATE INDEX idx_student_learning_events_curriculum_date
    ON student_learning_events (curriculum_framework_id, grade_level, event_date);

CREATE INDEX idx_student_learning_events_event_type_date
    ON student_learning_events (event_type, event_date);

CREATE INDEX idx_student_learning_events_subject_topic_skill
    ON student_learning_events (subject_id, topic_id, skill_id);
```

## Date strategy

```text
event_time          exact event timestamp
event_date          daily aggregation
event_year          yearly aggregation
event_month         monthly aggregation
event_day           day-of-month grouping
event_week          weekly aggregation
client_event_time   future mobile/offline support
server_received_at  ingestion/latency auditing
created_at          persistence timestamp
```

## Historical context strategy

Events must freeze context at event time. If a student changes country, grade, or curriculum later, old events must still represent the old historical state.

Therefore events store both IDs and context:

```text
curriculum_framework_id
curriculum_band_id
curriculum_expectation_id
country_code
grade_level
school_stage
age_at_event_months
complexity_level
depth_level
```

## Future data lake mapping

```text
PostgreSQL student_learning_events
  -> Parquet export
  -> bronze.learning_events
  -> silver.student_skill_daily
  -> gold.curriculum_benchmarks
```
