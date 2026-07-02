package com.boom.student.player.api.dto;

public record StudentPlayerActivityResponse(
        String activityId,
        String code,
        String title,
        String description,
        String subjectId,
        String topicId,
        String skillId,
        String objectiveId,
        String activityType,
        int estimatedDurationMinutes,
        String complexityLevel,
        String depthLevel
) {
}
