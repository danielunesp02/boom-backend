package com.boom.learning.api.dto;

public record QuestionOptionResponse(
        String optionId,
        String questionId,
        String label,
        String text,
        int displayOrder,
        String status
) {
}
