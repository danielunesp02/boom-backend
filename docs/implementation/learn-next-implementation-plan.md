# Learning Next Implementation Plan

## Recommended order

```text
1. LEARN-002 — Learning Activities and Questions
2. LEARN-003 — Student Learning Events Foundation
3. LEARN-004 — Assessment Attempt and Answer Submission
```

## Why this order

`LEARN-002` creates the content students interact with.

`LEARN-003` creates the analytical foundation before transactional attempts start generating data.

`LEARN-004` connects student execution to events.

## LEARN-002 checklist

```text
Migration V13
Domain records/enums
Repositories
Services
Read-only API
Dev seed
Tests
Commit validation update
```

Expected tables:

```text
learning_activities
activity_questions
question_options
```

## LEARN-003 checklist

```text
Migration V14
Event enum
StudentLearningEvent record
StudentLearningEventRepository
StudentLearningEventService
Tests for date fields and context freezing
```

Expected table:

```text
student_learning_events
```

## LEARN-004 checklist

```text
Migration V15
AssessmentAttempt
AnswerSubmission
Attempt service
Answer service
Attempt API
Access control using StudentAccessService
Event generation using StudentLearningEventService
Tests
```

Expected tables:

```text
assessment_attempts
answer_submissions
```

## Commit validation updates

Add smoke calls for:

```text
POST /api/v1/dev/seed/learning-activities
GET  /api/v1/learning/activities
GET  /api/v1/learning/activities/{activityId}
POST /api/v1/students/{studentId}/activities/{activityId}/attempts
POST /api/v1/attempts/{attemptId}/answers
POST /api/v1/attempts/{attemptId}/complete
```

## Frontend later

The frontend student activity flow should wait until backend LEARN-004 is stable.

Initial frontend scope later:

```text
Activity list
Activity detail
Question flow
Result screen
Parent dashboard with real recent activity
```
