package com.boom.learning.domain;

import java.time.Instant;
import java.util.UUID;

public record ActivityQuestion(
        UUID id,
        UUID activityId,
        String code,
        String prompt,
        String explanation,
        QuestionType questionType,
        UUID subjectId,
        UUID topicId,
        UUID skillId,
        UUID objectiveId,
        ComplexityLevel complexityLevel,
        DepthLevel depthLevel,
        int displayOrder,
        LearningStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
