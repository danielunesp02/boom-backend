package com.boom.pedagogy.domain;

import java.time.Instant;
import java.util.UUID;

public record StudentLearningStrategy(
        UUID id,
        UUID studentId,
        UUID methodologyId,
        LearningStrategyMode mode,
        String customConfigJson,
        boolean active,
        UUID createdByGuardianId,
        Instant createdAt,
        Instant updatedAt
) {
}
