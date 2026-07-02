# Architecture Overview Map

## Objetivo

Manter um mapa vivo da arquitetura do Boom, exportável diariamente e exibido no dashboard administrativo.

## Fonte da verdade recomendada

```text
architecture-map.yaml
```

A partir dela gerar:

```text
interactive HTML
SVG/PNG
Mermaid diagram
admin dashboard data
daily snapshot image
```

## Metadata de cada componente

```text
id
name
layer
type
status
owner
description
sourceCodePath
docsPath
apiPath
metricsPath
dependencies
relationships
```

## Status

```text
IMPLEMENTED
IN_PROGRESS
PLANNED
DEPRECATED
RISK
```

## Camadas

```text
frontend
backend
data
infrastructure
observability
ai
admin
```

## Comportamento HTML

```text
zoom
pan
click box to open details
filter by layer
filter by status
search by component name
show relationships
open source/doc/API links
```

## Stories

```text
ARCH-001 Architecture Map Source YAML
ARCH-002 Generate Static HTML Architecture Map
ARCH-003 Generate PNG/SVG Snapshot
ARCH-004 Admin Architecture Overview Page
ARCH-005 Link Component Health to Observability
```
