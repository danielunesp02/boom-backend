# PED-003 — Review Queue Due/Overdue Maintenance

## Goal

Make the review queue operational over time.

PED-002 creates review items. PED-003 updates those items as time passes.

## What it does

The maintenance service updates `student_review_queue` based on `scheduled_for` and methodology config:

```text
scheduled_for > today
  -> SCHEDULED

scheduled_for = today
  -> DUE

scheduled_for < today
  -> OVERDUE

overdue_days > maxOverdueDaysBeforeReassessment
  -> CRITICAL_OVERDUE
  -> requires_reassessment = true
```

When a review becomes critical overdue, the related `student_skill_mastery_state` is marked for reassessment.

## Dev endpoint

```http
POST /api/v1/dev/pedagogy/review-queue/refresh
POST /api/v1/dev/pedagogy/review-queue/refresh?date=2026-07-05
```

## Why this matters

A review that is missed does not disappear.

It becomes more important and can force a quick diagnostic or reentry assessment.

## Next product story

```text
DASH-004 — Real Learning Gaps from Mastery State
```

DASH-004 can consume:

```text
student_skill_mastery_state
student_review_queue
student_learning_interventions
```

and show real active gaps in the parent dashboard.
