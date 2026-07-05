# DASH-002 — Parent Dashboard Real Recent Activities

## Goal

Make the `Recent activities` section use real completed assessment attempts.

## Data source

This story uses:

```text
assessment_attempts
answer_submissions
learning_activities
learning_subjects
```

## Behavior

When real completed attempts exist in the selected dashboard period, `Recent activities` shows the latest completed activities.

When no real completed attempts exist, the dashboard keeps the previous mock fallback.

## Fields mapped

```text
activityId       <- assessment_attempts.activity_id
date             <- assessment_attempts.completed_at::date
activityTitle    <- learning_activities.title
subjectName      <- learning_subjects.default_name
accuracy         <- assessment_attempts.accuracy
durationMinutes  <- sum(answer_submissions.time_spent_seconds), fallback estimated duration
summaryText      <- correct_answers / answered_questions
```

## Validate

```bash
./mvnw test
```

Then run the frontend E2E:

```bash
cd ~/projects/boom/boom-frontend
npx playwright test e2e/student-flow-updates-parent-dashboard.spec.ts --workers=1
```

## Manual validation

Complete an activity in the student player.

Open the parent dashboard.

Expected:

```text
Recent activities shows the actual completed activity
Recent activities shows real subject name
Recent activities shows real accuracy
Recent activities shows real duration
```

## Commit

```bash
cd ~/projects/boom/boom-backend

git add \
  src/main/java/com/boom/parentdashboard/realdata/ParentDashboardRealMetrics.java \
  src/main/java/com/boom/parentdashboard/realdata/ParentDashboardRealDataAdapter.java \
  src/main/java/com/boom/parentdashboard/application/ParentDashboardMockService.java \
  docs/dash002-real-recent-activities.md

git commit -m "feat: use real recent activities in parent dashboard"
```
