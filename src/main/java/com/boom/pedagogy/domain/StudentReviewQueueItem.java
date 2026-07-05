package com.boom.pedagogy.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record StudentReviewQueueItem(
        UUID id,
        UUID studentId,
        UUID subjectId,
        UUID topicId,
        UUID skillId,
        UUID sourceAttemptId,
        UUID sourceEventId,
        ReviewReason reason,
        ReviewPriority priority,
        LocalDate scheduledFor,
        ReviewQueueStatus status,
        ReviewType reviewType,
        String methodologyCode,
        int overdueDays,
        boolean requiresReassessment,
        Instant createdAt,
        Instant completedAt,
        Instant updatedAt
) {
}
