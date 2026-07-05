package com.boom.pedagogy.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record StudentSkillMasteryState(
        UUID id,
        UUID studentId,
        UUID subjectId,
        UUID topicId,
        UUID skillId,
        MasteryStatus masteryStatus,
        BigDecimal masteryScore,
        BigDecimal accuracy,
        int questionsAnswered,
        int correctAnswers,
        int incorrectAnswers,
        int consecutiveSuccesses,
        int consecutiveFailures,
        int successfulReviews,
        int failedReviews,
        int reviewStepIndex,
        Integer reviewIntervalDays,
        Instant lastPracticedAt,
        Instant nextReviewAt,
        boolean requiresReassessment,
        BigDecimal confidenceScore,
        Instant createdAt,
        Instant updatedAt
) {
}
