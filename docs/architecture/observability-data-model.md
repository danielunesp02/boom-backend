# Observability Data Model

## Product usage events

```text
product_usage_events
```

Campos:

```text
id
event_type
actor_type
actor_id
tenant_id
student_id
teacher_id
guardian_id
feature_key
route
subject_id
topic_id
skill_id
activity_id
event_time
event_date
metadata_json
```

## Frontend performance events

```text
frontend_performance_events
```

Campos:

```text
id
session_id
actor_id
route
feature_key
browser
device_type
locale
load_time_ms
render_time_ms
api_wait_time_ms
error_count
event_time
metadata_json
```

## API performance events

```text
api_performance_events
```

Campos:

```text
id
request_id
trace_id
endpoint
method
status_code
latency_ms
actor_type
actor_id
tenant_id
error_code
event_time
metadata_json
```

## Feature health snapshots

```text
feature_health_snapshots
```

Campos:

```text
id
feature_key
period_start
period_end
request_count
success_count
error_count
p50_latency_ms
p95_latency_ms
p99_latency_ms
availability_percentage
anomaly_detected
anomaly_reason
created_at
```

## Separação

Eventos pedagógicos respondem:

```text
What is happening with students and learning?
```

Eventos técnicos respondem:

```text
Is the platform healthy and fast?
```

Eles se conectam por:

```text
session_id
request_id
trace_id
feature_key
actor_id
tenant_id
```
