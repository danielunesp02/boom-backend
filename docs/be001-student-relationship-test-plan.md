# BE-001 Student Relationship Test Plan

## Manual tests

### Unauthenticated list should fail

```bash
curl -i http://localhost:8080/api/v1/parents/students
```

Expected: `401` or `403`.

### Login

```bash
curl -i -c /tmp/boom-cookies.txt -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"identifier":"daniel.test","password":"BoomTest123!"}'
```

### Seed Helena

```bash
curl -i -b /tmp/boom-cookies.txt -X POST http://localhost:8080/api/v1/dev/seed/helena
```

### List students

```bash
curl -i -b /tmp/boom-cookies.txt http://localhost:8080/api/v1/parents/students
```

Expected: Helena linked as primary student.
