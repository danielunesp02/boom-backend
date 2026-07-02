package com.boom.student.attempt.api.dto;

public record SubmitAnswerRequest(
        String questionId,
        String selectedOptionId,
        String textAnswer,
        Integer timeSpentSeconds
) {
}
