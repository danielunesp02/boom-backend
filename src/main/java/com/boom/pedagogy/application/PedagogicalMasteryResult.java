package com.boom.pedagogy.application;

import java.time.LocalDate;
import java.util.UUID;

public record PedagogicalMasteryResult(
        UUID studentId,
        UUID activityId,
        UUID subjectId,
        UUID topicId,
        UUID skillId,
        String methodologyCode,
        String masteryStatus,
        String reviewType,
        String priority,
        String reason,
        LocalDate nextReviewDate,
        UUID reviewQueueId,
        UUID interventionId
) {
}
