package com.boom.student.snapshot.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record StudentSkillDailySnapshot(
        UUID id,
        LocalDate snapshotDate,
        UUID studentId,
        UUID guardianId,
        UUID subjectId,
        UUID topicId,
        UUID skillId,
        String gradeLevel,
        String targetSchoolSystem,
        String countryCode,
        String locale,
        int questionsAnswered,
        int correctAnswers,
        int incorrectAnswers,
        BigDecimal accuracy,
        BigDecimal totalScore,
        BigDecimal averageScore,
        int totalTimeSpentSeconds,
        BigDecimal averageTimeSpentSeconds,
        int activitiesStarted,
        int activitiesCompleted,
        int attemptsCompleted,
        Instant firstEventAt,
        Instant lastEventAt,
        Instant createdAt,
        Instant updatedAt
) {}
