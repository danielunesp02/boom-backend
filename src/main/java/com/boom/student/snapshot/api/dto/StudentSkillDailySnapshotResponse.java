package com.boom.student.snapshot.api.dto;

public record StudentSkillDailySnapshotResponse(
        String snapshotId,
        String snapshotDate,
        String studentId,
        String guardianId,
        String subjectId,
        String topicId,
        String skillId,
        String gradeLevel,
        String targetSchoolSystem,
        String countryCode,
        String locale,
        int questionsAnswered,
        int correctAnswers,
        int incorrectAnswers,
        double accuracy,
        double totalScore,
        double averageScore,
        int totalTimeSpentSeconds,
        double averageTimeSpentSeconds,
        int activitiesStarted,
        int activitiesCompleted,
        int attemptsCompleted,
        String firstEventAt,
        String lastEventAt
) {}
