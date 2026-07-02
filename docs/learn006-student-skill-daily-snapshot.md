# LEARN-006 — Student Skill Daily Snapshot

## Goal

Create the first analytical snapshot layer for Boom.

Events are immutable history. Snapshots are query-friendly aggregates for dashboards and recommendations.

## Input

```text
student_learning_events
```

## Output

```text
student_skill_daily_snapshots
```

One row per:

```text
student
skill
date
```

## Metrics

```text
questions_answered
correct_answers
incorrect_answers
accuracy
total_score
average_score
total_time_spent_seconds
average_time_spent_seconds
activities_started
activities_completed
attempts_completed
first_event_at
last_event_at
```

## Endpoints

```http
POST /api/v1/dev/snapshots/student-skills/daily/rebuild?date=YYYY-MM-DD
GET  /api/v1/students/{studentId}/snapshots/skills/daily?date=YYYY-MM-DD
```

## Next stories

```text
LEARN-007 — Parent Dashboard Real Data Adapter
LEARN-008 — Learning Gap Detector Foundation
STUDENT-005 — Player Review Mode
```
