package com.boom.pedagogy.domain;

public enum ReviewQueueStatus {
    SCHEDULED,
    DUE,
    OVERDUE,
    CRITICAL_OVERDUE,
    COMPLETED,
    CANCELLED,
    SUPERSEDED
}
