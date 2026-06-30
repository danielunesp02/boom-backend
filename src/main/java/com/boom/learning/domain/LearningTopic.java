package com.boom.learning.domain;

import java.time.Instant;
import java.util.UUID;

public record LearningTopic(
        UUID id,
        UUID subjectId,
        String code,
        String defaultName,
        String description,
        int displayOrder,
        LearningStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
