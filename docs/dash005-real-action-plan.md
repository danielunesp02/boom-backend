# DASH-005 — Real Action Plan from Review Queue

## Goal

Make the `Current action plan` card use real pedagogical recommendations.

## Source

This story reuses `ParentDashboardRealMetrics.learningGapMetrics()` from DASH-004.

That metric is sourced from:

```text
student_skill_mastery_state
student_review_queue
learning_subjects
learning_topics
learning_skills
```

## Product behavior

The dashboard selects the first prioritized learning gap and turns it into a concrete action plan.

The action plan adapts to the review type:

```text
WORKED_EXAMPLE
GUIDED_PRACTICE
RETRIEVAL_QUIZ
MASTERY_CHECK
QUICK_DIAGNOSTIC
REENTRY_ASSESSMENT
```

If there is no real learning gap yet, the dashboard keeps the previous fallback action plan.
