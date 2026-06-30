# LEARN-001 — Learning Taxonomy and Curriculum Foundation

Modelo:

```text
Subject → Topic → Skill → LearningObjective
CurriculumFramework → CurriculumBand → CurriculumExpectation
```

Decisão importante: a futura `student_learning_events` deve referenciar todos os objetos core e congelar o contexto histórico do evento:

```text
student_id, guardian_id, session_id, attempt_id, answer_submission_id,
subject_id, topic_id, skill_id, learning_objective_id,
activity_id, question_id,
curriculum_framework_id, curriculum_band_id, curriculum_expectation_id,
event_time, event_date, event_year, event_month, event_day, event_week,
client_event_time, server_received_at,
country_code, grade_level, school_stage, age_at_event_months,
knowledge_level_before, knowledge_level_after,
complexity_level, depth_level, source_channel, locale
```

Próximas stories sugeridas:

```text
LEARN-002 — Learning Activities and Questions
LEARN-003 — Student Learning Events Foundation
LEARN-004 — Assessment Attempt and Answer Submission
LEARN-005 — Student Skill Daily Snapshots
```
