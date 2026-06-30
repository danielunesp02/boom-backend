package com.boom.learning.domain;

import java.time.Instant;
import java.util.UUID;

public record LearningSubject(
        UUID id,
        String code,
        String defaultName,
        String description,
        LearningStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
