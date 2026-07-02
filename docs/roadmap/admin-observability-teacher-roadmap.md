# Roadmap — Admin, Observability and Teacher Profile

## Ordem recomendada

```text
1. LEARN-003 Student Learning Events
2. LEARN-004 Assessment Attempts and Answer Submissions
3. OBS-001 Backend Actuator/Micrometer
4. OBS-002 Grafana Dashboard Foundation
5. TEACH-001 Teacher Account
6. TEACH-002 School/Classroom
7. TEACH-003 Classroom Enrollment
8. ADM-001 Admin Home Overview
9. TEACH-004 Teacher Reports Overview
10. OBS-007 Admin Observability UI
11. ARCH-001 Source YAML
12. ARCH-002 Interactive HTML
13. ARCH-004 Admin Architecture Overview Page
```

## Dependência importante

Teacher reports ficam muito melhores depois de:

```text
student_learning_events
assessment_attempts
answer_submissions
learning_gaps
action_plans
```

## MVP recomendado

```text
teacher can log in
teacher can see assigned classroom
teacher can see students
teacher can see basic performance summary
admin can see platform KPIs
admin can open architecture map
admin can see basic API health
```

## Evitar no MVP

```text
advanced school billing
complex multi-tenant hierarchy
complex RBAC editor
AI recommendations to teachers
full anomaly ML
```
