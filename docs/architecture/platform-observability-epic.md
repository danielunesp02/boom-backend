# EPIC — Platform Observability Dashboard

## Objetivo

Criar uma visão de observabilidade dentro do Boom para monitorar frontend, APIs, banco, Redis, IA, automações, deploys, features e anomalias.

## Métricas por camada

### Frontend

```text
page load time
route transition time
time to interactive
dashboard render time
activity player render time
JS errors by page
API latency perceived by browser
signup step abandonment
login success/failure
feature usage events
```

### Backend APIs

```text
requests per endpoint
p50 latency
p95 latency
p99 latency
4xx rate
5xx rate
timeout rate
auth failures
slowest endpoints
most used endpoints
```

### Database

```text
connections in use
slow queries
query duration
locks
table growth
index usage
cache hit ratio
dead tuples
student_learning_events insert rate
```

### Redis

```text
memory usage
hit ratio
evictions
latency
connection count
command frequency
cache effectiveness
```

### AI integrations

```text
request count
latency
error rate
token usage
cost estimate
provider availability
fallback rate
response quality flags
```

### Automations and Jobs

```text
job success rate
job failure rate
retry count
queue lag
dead-letter count
execution duration
last successful run
```

## Anomalias iniciais

```text
p95 latency > baseline + 50%
5xx rate > 2%
login failure rate spikes
dashboard render time spikes
student activity completion drops suddenly
Redis evictions > 0
database connections > 80%
AI token usage > daily budget
```

## Stories

```text
OBS-001 Backend Actuator and Metrics Foundation
OBS-002 Grafana Dashboard Foundation
OBS-003 Frontend Telemetry Events
OBS-004 Feature Health Dashboard
OBS-005 Anomaly Detection Rules
OBS-006 Architecture Component Status API
OBS-007 Admin Observability UI
```
