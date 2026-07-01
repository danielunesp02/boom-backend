# LEARN-002 — Learning Activities and Questions

## Purpose

This story creates the first layer of executable learning content.

Boom now has taxonomy and curriculum. This story adds:

```text
LearningActivity
ActivityQuestion
QuestionOption
```

## Data model

```text
learning_activities
  id
  code
  title
  description
  subject_id
  topic_id
  skill_id
  objective_id
  curriculum_framework_id
  curriculum_band_id
  curriculum_expectation_id
  activity_type
  estimated_duration_minutes
  complexity_level
  depth_level
  display_order
  status

activity_questions
  id
  activity_id
  code
  prompt
  explanation
  question_type
  subject_id
  topic_id
  skill_id
  objective_id
  complexity_level
  depth_level
  display_order
  status

question_options
  id
  question_id
  label
  option_text
  is_correct
  display_order
  status
```

## API

```http
GET  /api/v1/learning/activities
GET  /api/v1/learning/activities/{activityId}
POST /api/v1/dev/seed/learning-activities
```

## Seeded examples

```text
Mathematics / Fractions / Equivalent fractions
- Visual fraction comparison practice
- Which fraction is equivalent to 1/2?
- Which fraction is equivalent to 3/6?

English / Reading comprehension / Main idea
- Main idea short text practice
- Bees help plants reproduce main idea question
```

## Next

LEARN-003 should add:

```text
student_learning_events
```

LEARN-004 should add:

```text
assessment_attempts
answer_submissions
```
