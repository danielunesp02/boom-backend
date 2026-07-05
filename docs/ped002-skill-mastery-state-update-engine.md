# PED-002 — Skill Mastery State Update Engine

## Goal

Turn a completed assessment attempt into real pedagogical state.

PED-001 created the schema. PED-002 starts filling it automatically when the student completes an activity.

## Integration point

`AssessmentAttemptService.completeAttempt(...)`

PED-002 adds:

```java
pedagogicalMasteryUpdateService.updateAfterAttemptCompleted(completed);
```

## What is updated

### `student_skill_mastery_state`

Initial decision rules:

```text
accuracy >= masteryThreshold -> REVIEW_SCHEDULED or MASTERED
accuracy >= 75 and below threshold -> LEARNING
accuracy >= 60 and below 75 -> ACTIVE_GAP / MEDIUM priority
accuracy < 60 -> ACTIVE_GAP / HIGH priority
```

### `student_review_queue`

Creates the next review item unless the skill is already considered mastered.

### `student_learning_interventions`

Registers an auditable intervention with `ai_used = false` for now.

## Methodology selection

The engine looks for an active `student_learning_strategy`. If none exists, it falls back to `BALANCED`.

## Next story

```text
DASH-004 — Real Learning Gaps from Mastery State
```
