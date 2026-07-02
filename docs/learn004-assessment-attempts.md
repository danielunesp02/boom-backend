# LEARN-004 — Assessment Attempt + Answer Submission

## Goal

Persist the first real student learning flow.

The student activity player can now:

```text
start an attempt
submit an answer
receive backend correctness
receive mock AI feedback
complete the attempt
receive score and accuracy
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

## Important security rule

Correct answers are never sent in the player payload.

Correctness is calculated only in the backend during answer submission.

## Mock AI feedback

This story includes mock answer feedback:

```text
correct answer -> encouraging explanation
incorrect answer -> supportive review explanation
```

Later, this will be replaced or enhanced by a real AI provider through a service contract.

## Current access model

Only a guardian with a valid relationship to the student can:

```text
start an attempt
submit answers
complete an attempt
view an attempt
```

## Next story

LEARN-005 should add:

```text
student_learning_events
```

Events to emit:

```text
ACTIVITY_STARTED
QUESTION_ANSWERED
ACTIVITY_COMPLETED
HINT_VIEWED
```
