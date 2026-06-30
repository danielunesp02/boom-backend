package com.boom.learning.domain;

import java.time.Instant;
import java.util.UUID;

public record CurriculumExpectation(
        UUID id,
        UUID bandId,
        UUID subjectId,
        UUID topicId,
        UUID skillId,
        UUID objectiveId,
        KnowledgeLevel expectedKnowledgeLevel,
        ComplexityLevel expectedComplexityLevel,
        DepthLevel expectedDepthLevel,
        CurriculumPriority priority,
        LearningStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
