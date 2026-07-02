# LEARN-007 Integration Guide

## Step 1 — Find dashboard service

```bash
find src/main/java -path '*dashboard*' -type f
```

Likely candidates:

```text
ParentDashboardService
ParentDashboardMockService
ParentDashboardController
```

## Step 2 — Inject adapter

```java
private final ParentDashboardRealDataAdapter realDataAdapter;
```

Constructor:

```java
public ParentDashboardService(..., ParentDashboardRealDataAdapter realDataAdapter) {
    ...
    this.realDataAdapter = realDataAdapter;
}
```

## Step 3 — Load real metrics

After resolving student:

```java
ParentDashboardRealMetrics realMetrics = realDataAdapter.loadForStudent(student.id(), 30);
```

## Step 4 — Replace KPI cards

Mapping suggestion:

```text
Completed activities -> realMetrics.completedActivities()
Study time -> realMetrics.totalTimeSpentSeconds()
Accuracy -> realMetrics.accuracy()
Active gaps -> keep mock until LEARN-008
```

## Step 5 — Replace subject performance

Use:

```java
realMetrics.subjectMetrics()
```

Mapping:

```text
subjectName
accuracy
totalTimeSpentSeconds
completedActivities
```

## Step 6 — Keep fallback

```java
if (!realMetrics.hasRealData()) {
    return currentMockDashboard;
}
```

## Step 7 — Manual validation

```bash
curl -i -b /tmp/boom-cookies.txt -X POST \
  "http://localhost:8080/api/v1/dev/snapshots/student-skills/daily/rebuild"
```

Then open dashboard.
