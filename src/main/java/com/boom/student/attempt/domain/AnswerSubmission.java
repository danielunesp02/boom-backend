package com.boom.student.attempt.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AnswerSubmission(
        UUID id,
        UUID attemptId,
        UUID questionId,
        UUID selectedOptionId,
        String textAnswer,
        boolean correct,
        BigDecimal score,
        Integer timeSpentSeconds,
        Instant submittedAt,
        Instant createdAt,
        Instant updatedAt
) {
}
