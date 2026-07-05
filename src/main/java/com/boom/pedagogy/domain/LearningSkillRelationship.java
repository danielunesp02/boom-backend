package com.boom.pedagogy.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record LearningSkillRelationship(
        UUID id,
        UUID sourceSkillId,
        UUID targetSkillId,
        SkillRelationshipType relationshipType,
        BigDecimal weight,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
