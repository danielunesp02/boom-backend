package com.boom.learning.domain;

import java.time.Instant;
import java.util.UUID;

public record CurriculumFramework(
        UUID id,
        String countryCode,
        String code,
        String name,
        String version,
        CurriculumSourceType sourceType,
        LearningStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
