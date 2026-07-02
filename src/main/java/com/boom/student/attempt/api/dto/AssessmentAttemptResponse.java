package com.boom.student.attempt.api.dto;

import java.util.List;

public record AssessmentAttemptResponse(
        String attemptId,
        String studentId,
        String activityId,
        String status,
        String sourceChannel,
        String locale,
        int totalQuestions,
        int answeredQuestions,
        int correctAnswers,
        double score,
        double accuracy,
        String startedAt,
        String completedAt,
        List<AnswerSubmissionResponse> answers
) {
}
