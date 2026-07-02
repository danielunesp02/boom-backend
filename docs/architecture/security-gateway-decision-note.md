# Architecture Decision Note — Security Gateway Layer

## Context

Boom will grow from a single backend into a platform with:

```text
frontend app
student flow
parent dashboard
teacher dashboard
admin dashboard
learning APIs
AI integrations
observability
background jobs
possibly multiple services later
```

Security becomes a sensitive architectural point.

## Decision

Create a security gateway concept, but do not start with a separate gateway microservice yet.

Initial approach:

```text
Security Gateway Layer inside the backend
```

Future approach:

```text
Dedicated API Gateway / Edge Gateway when service boundaries justify it
```

## Why not a separate gateway now?

A separate gateway too early adds:

```text
more deployment complexity
more local development friction
more configuration surface
more observability needs
more failure points
```

Boom is still early and currently benefits more from:

```text
strong backend security boundaries
simple deployment
fast iteration
explicit access control
good audit trail
```

## What the internal Security Gateway Layer should centralize

```text
authentication
session validation
role resolution
student access control
teacher access control
admin access control
rate limiting rules
audit events
tenant/school context
feature access
sensitive route protection
AI usage permissions
```

## Gateway-sensitive rules

The backend must never trust frontend-only rules.

Every sensitive operation must validate:

```text
who is the actor?
what role do they have?
which student/class/school/tenant are they accessing?
what relationship grants access?
is the action allowed?
should the action be audited?
does this consume AI budget or sensitive data?
```

## Suggested future stories

```text
SEC-001 Security Gateway Layer Foundation
SEC-002 Central Access Policy Service
SEC-003 Student Access Policy
SEC-004 Teacher/Classroom Access Policy
SEC-005 Admin/Tenant Access Policy
SEC-006 Audit Event Foundation
SEC-007 Rate Limit and Abuse Protection
SEC-008 External API Gateway Evaluation
```

## Opinion

A gateway is a good idea, but the correct first step is not infrastructure. The correct first step is a clear security policy layer in the backend.

When Boom grows into multiple services, the same policies can be enforced at:

```text
edge gateway
backend services
admin UI
observability dashboards
```
