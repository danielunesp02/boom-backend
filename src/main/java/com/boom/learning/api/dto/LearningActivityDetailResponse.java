package com.boom.learning.api.dto;

import java.util.List;

public record LearningActivityDetailResponse(
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
        String status,
        List<ActivityQuestionResponse> questions
) {
}
