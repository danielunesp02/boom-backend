package com.boom.student.learningevent.application;

import com.boom.student.learningevent.domain.StudentLearningEventType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record StudentLearningEventCommand(
        UUID studentId,
        UUID guardianId,
        UUID assessmentAttemptId,
        UUID answerSubmissionId,
        UUID activityId,
        UUID questionId,
        StudentLearningEventType eventType,
        Instant clientEventTime,
        Boolean correct,
        BigDecimal score,
        Integer timeSpentSeconds,
        Integer attemptNumber,
        String sourceChannel,
        String locale,
        String metadataJson
) {
}
