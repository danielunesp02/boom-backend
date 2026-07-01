package com.boom.learning.api.dto;

public record LearningActivitySummaryResponse(
        String activityId,
        String code,
        String title,
        String description,
        String subjectId,
        String topicId,
        String skillId,
        String objectiveId,
        String curriculumFrameworkId,
        String curriculumBandId,
        String curriculumExpectationId,
        String activityType,
        int estimatedDurationMinutes,
        String complexityLevel,
        String depthLevel,
        int displayOrder,
        String status
) {
}
