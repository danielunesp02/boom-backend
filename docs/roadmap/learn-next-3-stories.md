# Next Learning Stories

## Context

Boom already has guardian authentication, guardian/student relationship, authenticated parent dashboard, learning taxonomy, and curriculum foundation.

The next step is to transform Boom from dashboard + taxonomy into a learning execution platform.

---

# LEARN-002 — Learning Activities and Questions

## Goal

Create the content structure that students can study and answer.

## Entities

```text
LearningActivity
ActivityQuestion
QuestionOption
```

## Tables

```text
learning_activities
activity_questions
question_options
```

## Key relationships

```text
LearningActivity
  subject_id
  topic_id
  skill_id
  objective_id
  curriculum_framework_id optional
  curriculum_band_id optional
  curriculum_expectation_id optional

ActivityQuestion
  activity_id
  subject_id
  topic_id
  skill_id
  objective_id
  complexity_level
  depth_level

QuestionOption
  question_id
  label
  text
  is_correct
```

## Endpoints

```http
GET  /api/v1/learning/activities
GET  /api/v1/learning/activities/{activityId}
POST /api/v1/dev/seed/learning-activities
```

## Acceptance criteria

- Activities are linked to taxonomy and curriculum objects.
- Questions are linked to activities and learning objectives.
- Multiple-choice options can be seeded.
- API returns activity details with questions and options.
- Seed is idempotent.
- Backend tests pass.
- Commit validation smoke can seed and query activities.

---

# LEARN-003 — Student Learning Events Foundation

## Goal

Create the append-only analytical event table that becomes the future source of truth for learning analytics and data science.

## Table

```text
student_learning_events
```

## Event types

```text
ACTIVITY_STARTED
QUESTION_ANSWERED
ACTIVITY_COMPLETED
SKILL_EVIDENCE_GENERATED
GAP_DETECTED
GAP_RESOLVED
MASTERY_LEVEL_CHANGED
EXPLANATION_VIEWED
HINT_USED
```

## Core decisions

- Append-only.
- Never update or delete events in normal flow.
- Events freeze historical context at the time of the action.
- Events reference all core learning objects when available.
- Events include date fields optimized for daily, monthly, yearly aggregation.
- Metadata uses JSONB for controlled flexibility.

## Acceptance criteria

- Migration creates `student_learning_events`.
- Repository supports appending events.
- Service centralizes event creation.
- Event date fields are derived consistently from `event_time`.
- Tests verify event creation and historical context persistence.

---

# LEARN-004 — Assessment Attempt and Answer Submission

## Goal

Create the student execution flow for answering an activity.

## Entities

```text
AssessmentAttempt
AnswerSubmission
```

## Tables

```text
assessment_attempts
answer_submissions
```

## Endpoints

```http
POST /api/v1/students/{studentId}/activities/{activityId}/attempts
POST /api/v1/attempts/{attemptId}/answers
POST /api/v1/attempts/{attemptId}/complete
GET  /api/v1/attempts/{attemptId}
```

## Event integration

```text
POST attempt
  -> ACTIVITY_STARTED

POST answer
  -> QUESTION_ANSWERED

POST complete
  -> ACTIVITY_COMPLETED
```

## Acceptance criteria

- Authenticated guardian can start attempt for an owned student.
- Attempt references student, activity, subject, topic, skill, objective.
- Answer submission validates question belongs to activity.
- Correctness is calculated from question option.
- Events are generated for start, answer, and completion.
- API returns attempt state.
