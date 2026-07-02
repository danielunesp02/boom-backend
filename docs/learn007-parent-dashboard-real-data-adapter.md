# LEARN-007 — Parent Dashboard Real Data Adapter

## Current state

The full student learning flow now works:

```text
Student Activity Player
Assessment Attempt
Answer Submission
Student Learning Events
Student Skill Daily Snapshots
```

But the parent dashboard still mostly uses mock data.

## Goal

Start connecting the parent dashboard to real student learning data.

## Data source

```text
student_skill_daily_snapshots
```

## Adapter

```text
ParentDashboardRealDataAdapter
```

It loads:

```text
summary metrics
subject metrics
skill metrics
```

## Safe hybrid strategy

The dashboard should behave like this:

```text
real snapshots exist -> use real metrics
no snapshots exist -> fallback to current mock dashboard
```

## First real dashboard values

After a student completes the fractions activity and daily snapshots are rebuilt, the dashboard should be able to show:

```text
Completed activities: 1
Accuracy: 100%
Study time: answer time spent
Subject performance: Mathematics 100%
Skill: Equivalent fractions 100%
```

## Suggested integration

Find the current parent dashboard service:

```bash
find src/main/java -path '*dashboard*' -type f
```

Look for the method that builds the dashboard response.

Inject:

```java
private final ParentDashboardRealDataAdapter realDataAdapter;
```

Load real metrics after resolving the student:

```java
ParentDashboardRealMetrics realMetrics = realDataAdapter.loadForStudent(student.id(), 30);
```

Then:

```java
if (realMetrics.hasRealData()) {
    // Replace top-level dashboard metrics and subject performance with real values.
}
```

## Why incremental

Do not replace the whole dashboard at once.

Replace in this order:

```text
1. Top KPI cards
2. Subject performance
3. Recent activity summaries
4. Learning gaps
5. Action plan
```

## Next story

```text
LEARN-008 — Learning Gap Detector Foundation
```
