package com.boom.learning.domain;

import java.time.Instant;
import java.util.UUID;

public record CurriculumBand(
        UUID id,
        UUID frameworkId,
        String code,
        int minAgeMonths,
        int maxAgeMonths,
        String gradeLevel,
        String schoolStage,
        int displayOrder,
        LearningStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
