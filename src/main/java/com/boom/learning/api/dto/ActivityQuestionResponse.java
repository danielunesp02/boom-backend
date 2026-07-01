package com.boom.learning.api.dto;

import java.util.List;

public record ActivityQuestionResponse(
        String questionId,
        String activityId,
        String code,
        String prompt,
        String explanation,
        String questionType,
        String subjectId,
        String topicId,
        String skillId,
        String objectiveId,
        String complexityLevel,
        String depthLevel,
        int displayOrder,
        String status,
        List<QuestionOptionResponse> options
) {
}
