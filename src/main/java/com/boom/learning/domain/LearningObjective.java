package com.boom.learning.domain;

import java.time.Instant;
import java.util.UUID;

public record LearningObjective(
        UUID id,
        UUID skillId,
        String code,
        String description,
        ComplexityLevel complexityLevel,
        DepthLevel depthLevel,
        int displayOrder,
        LearningStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
