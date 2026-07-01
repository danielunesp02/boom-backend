package com.boom.learning.domain;

import java.time.Instant;
import java.util.UUID;

public record QuestionOption(
        UUID id,
        UUID questionId,
        String label,
        String text,
        boolean correct,
        int displayOrder,
        LearningStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
