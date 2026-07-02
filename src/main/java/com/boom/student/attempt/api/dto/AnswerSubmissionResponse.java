package com.boom.student.attempt.api.dto;

public record AnswerSubmissionResponse(
        String answerSubmissionId,
        String attemptId,
        String questionId,
        String selectedOptionId,
        String textAnswer,
        boolean correct,
        double score,
        Integer timeSpentSeconds,
        String submittedAt,
        AiCoachFeedbackResponse aiFeedback
) {
}
