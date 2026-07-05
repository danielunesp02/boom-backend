package com.boom.pedagogy.domain;

import java.time.Instant;
import java.util.UUID;

public record LearningMethodology(
        UUID id,
        String code,
        String name,
        String description,
        String configJson,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
