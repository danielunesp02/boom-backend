# DASH-006A — Subject Strategy Selector + Learning Curves Foundation

Backend foundation for per-subject methodology selection and learning curves.

## Endpoints

```http
GET /api/v1/students/{studentId}/subject-learning-strategies
GET /api/v1/students/{studentId}/subject-learning-strategies/{subjectId}
PUT /api/v1/students/{studentId}/subject-learning-strategies
```

## Methodologies

```text
LIGHT_REVIEW
BALANCED
INTENSIVE_REMEDIATION
EXAM_PREP
```

## Curves

Each response returns:

```text
actualMasteryScore
expectedMasteryScore
highPerformanceScore
```

The MVP uses interpretable parametric curves. Later, these targets can be calibrated using cohort percentiles.
