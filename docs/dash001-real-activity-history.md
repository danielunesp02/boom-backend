# DASH-001 — Parent Dashboard Real Activity History

## Goal

Make the `Activity history` section use real student learning data from:

```text
student_skill_daily_snapshots
```

Previously, the dashboard used real data for:

```text
Completed activities
Study time
Accuracy
Subject performance
```

but still used mocked data for:

```text
Activity history
```

## What changed

### `ParentDashboardRealMetrics`

Adds:

```java
DailyActivityMetric
```

with:

```text
snapshotDate
completedActivities
accuracy
totalTimeSpentSeconds
```

### `ParentDashboardRealDataAdapter`

Adds:

```java
loadDailyActivityHistory(...)
```

This groups `student_skill_daily_snapshots` by `snapshot_date`.

### `ParentDashboardMockService`

Changes response assembly from:

```java
activityHistory(period)
```

to:

```java
realMetrics.hasRealData() ? activityHistory(realMetrics) : activityHistory(period)
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

## Commit

```bash
cd ~/projects/boom/boom-backend

git add \
  src/main/java/com/boom/parentdashboard/realdata/ParentDashboardRealMetrics.java \
  src/main/java/com/boom/parentdashboard/realdata/ParentDashboardRealDataAdapter.java \
  src/main/java/com/boom/parentdashboard/application/ParentDashboardMockService.java \
  docs/dash001-real-activity-history.md

git commit -m "feat: use real activity history in parent dashboard"
```
