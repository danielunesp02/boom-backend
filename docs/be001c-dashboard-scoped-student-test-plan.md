# BE-001C Dashboard Scoped Student Test Plan

## Manual test

### 1. Login

```bash
curl -i -c /tmp/boom-cookies.txt -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"identifier":"daniel.test","password":"BoomTest123!"}'
```

### 2. Seed Helena

```bash
curl -i -b /tmp/boom-cookies.txt -X POST http://localhost:8080/api/v1/dev/seed/helena
```

### 3. List students

```bash
curl -i -b /tmp/boom-cookies.txt http://localhost:8080/api/v1/parents/students
```

### 4. Dashboard

```bash
curl -i -b /tmp/boom-cookies.txt "http://localhost:8080/api/v1/parents/dashboard?periodPreset=LAST_30_DAYS"
```

Expected:

- `HTTP 200`
- `student.id` equals the student returned by `/api/v1/parents/students`
- `student.displayName` is `Helena Bevilacqua`
- `student.gradeLevel` is `GRADE_7`
- `student.targetSchoolSystem` is `ITALY`

### 5. Dashboard without cookie

```bash
curl -i "http://localhost:8080/api/v1/parents/dashboard?periodPreset=LAST_30_DAYS"
```

Expected: `401` or `403`.
