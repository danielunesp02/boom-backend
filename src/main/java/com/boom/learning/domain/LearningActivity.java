package com.boom.learning.domain;

import java.time.Instant;
import java.util.UUID;

public record LearningActivity(
        UUID id,
        String code,
        String title,
        String description,
        UUID subjectId,
        UUID topicId,
        UUID skillId,
        UUID objectiveId,
        UUID curriculumFrameworkId,
        UUID curriculumBandId,
        UUID curriculumExpectationId,
        LearningActivityType activityType,
        int estimatedDurationMinutes,
        ComplexityLevel complexityLevel,
        DepthLevel depthLevel,
        int displayOrder,
        LearningStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
