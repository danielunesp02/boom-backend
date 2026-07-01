# Learning Execution Sequence Diagram

```mermaid
sequenceDiagram
    autonumber

    actor Guardian as Guardian/User
    participant FE as Boom Frontend
    participant Auth as Auth/Security
    participant Student as StudentAccessService
    participant Catalog as LearningCatalogService
    participant Attempt as AssessmentAttemptService
    participant Event as StudentLearningEventService
    participant DB as PostgreSQL

    Guardian->>FE: Open activity
    FE->>Auth: Send BOOM_SESSION cookie
    Auth->>DB: Validate session
    DB-->>Auth: CurrentUser guardian
    Auth-->>FE: Authenticated session

    FE->>Catalog: GET /api/v1/learning/activities/{activityId}
    Catalog->>DB: Load activity + questions + options
    DB-->>Catalog: Activity detail
    Catalog-->>FE: Activity detail

    Guardian->>FE: Start activity for student
    FE->>Attempt: POST /api/v1/students/{studentId}/activities/{activityId}/attempts
    Attempt->>Auth: Resolve authenticated guardian
    Attempt->>Student: Check guardian owns student
    Student->>DB: guardian_student_relationships
    DB-->>Student: Access granted

    Attempt->>DB: Create assessment_attempt
    Attempt->>Event: Append ACTIVITY_STARTED
    Event->>DB: Insert student_learning_event
    Attempt-->>FE: Attempt started

    Guardian->>FE: Answer question
    FE->>Attempt: POST /api/v1/attempts/{attemptId}/answers
    Attempt->>DB: Load attempt + question + selected option
    Attempt->>Attempt: Calculate correctness
    Attempt->>DB: Insert answer_submission
    Attempt->>Event: Append QUESTION_ANSWERED
    Event->>DB: Insert student_learning_event
    Attempt-->>FE: Answer result

    Guardian->>FE: Complete activity
    FE->>Attempt: POST /api/v1/attempts/{attemptId}/complete
    Attempt->>DB: Aggregate answers
    Attempt->>DB: Mark attempt completed
    Attempt->>Event: Append ACTIVITY_COMPLETED
    Event->>DB: Insert student_learning_event
    Attempt-->>FE: Attempt summary

    FE->>FE: Show result screen
```

## Notes

- The event service should receive enough context to create a complete analytical event.
- The attempt service owns correctness calculation.
- The student access service protects student ownership.
- The event table is the source for analytics, not the transactional source for attempt state.
