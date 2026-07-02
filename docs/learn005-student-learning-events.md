# LEARN-005 — Student Learning Events Foundation

## Goal

Create Boom's append-only learning event foundation.

## Table

```text
student_learning_events
```

## Events emitted initially

```text
ACTIVITY_STARTED
QUESTION_ANSWERED
ACTIVITY_COMPLETED
```

## Why append-only

Learning events should preserve history.

If a student later changes:

```text
grade
country
target school system
curriculum
methodology
skill taxonomy
```

old events must still represent the context at the time of learning.

## Fields included

```text
student_id
guardian_id
assessment_attempt_id
answer_submission_id
subject_id
topic_id
skill_id
learning_objective_id
activity_id
question_id
curriculum_framework_id
curriculum_band_id
curriculum_expectation_id
event_type
event_time
event_date
event_year
event_month
event_day
event_week
client_event_time
server_received_at
grade_level
target_school_system
complexity_level
depth_level
is_correct
score
time_spent_seconds
source_channel
locale
metadata
created_at
```

## Future use

This table enables:

```text
real parent dashboard
teacher classroom reports
student progress timeline
learning gap detection
action plan generation
daily/monthly snapshots
curriculum benchmark analysis
AI-generated parent summaries
```

## Next story

Recommended next story:

```text
LEARN-006 — Student Skill Daily Snapshot
```

or, if product UX should continue first:

```text
STUDENT-004 — E2E Student Player Attempt Flow
```
