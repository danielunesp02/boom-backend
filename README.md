# Boom Backend Patch - Parent Dashboard Mock Endpoint

This patch adds the first parent dashboard API contract.

Endpoint:

```text
GET /api/v1/parents/dashboard
```

It returns realistic seed/mock data for the frontend PoC.

## Apply

```bash
cd ~/projects/boom/boom-backend
cp -R /tmp/boom-sprint4/boom-backend-patch/* .
```

## Run

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

## Test

```bash
curl http://localhost:8080/api/v1/parents/dashboard
```
