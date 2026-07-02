# Frontend Integration — LEARN-004

## Current player behavior

The player currently uses local state only.

## New backend flow

On opening the activity:

```http
POST /api/v1/students/{studentId}/activities/{activityId}/attempts
```

When the student chooses an answer and taps Check/Next:

```http
POST /api/v1/attempts/{attemptId}/answers
```

When the student finishes:

```http
POST /api/v1/attempts/{attemptId}/complete
```

## UX implication

The player should evolve from:

```text
Select -> Next
```

to:

```text
Select -> Check answer -> Feedback -> Next
```

## Suggested states

```text
selecting
submitting
submitted_correct
submitted_incorrect
moving_next
completed
```

## Feedback display

Use:

```text
response.correct
response.aiFeedback.tone
response.aiFeedback.message
```

The answer card should visually show:

```text
selected
correct
incorrect
```

But only after backend submission.
