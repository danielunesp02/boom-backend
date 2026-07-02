package com.boom.student.attempt.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AssessmentAttempt(
        UUID id,
        UUID studentId,
        UUID guardianId,
        UUID activityId,
        AssessmentAttemptStatus status,
        SourceChannel sourceChannel,
        String locale,
        int totalQuestions,
        int answeredQuestions,
        int correctAnswers,
        BigDecimal score,
        BigDecimal accuracy,
        Instant startedAt,
        Instant completedAt,
        Instant createdAt,
        Instant updatedAt
) {
}
