package com.boom.learning.domain;

import java.time.Instant;
import java.util.UUID;

public record LearningSkill(
        UUID id,
        UUID topicId,
        String code,
        String defaultName,
        String description,
        int displayOrder,
        LearningStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
