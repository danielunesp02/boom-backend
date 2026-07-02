package com.boom.student.player.api.dto;

import java.util.List;

public record StudentPlayerQuestionResponse(
        String questionId,
        String code,
        String prompt,
        String questionType,
        String complexityLevel,
        String depthLevel,
        int displayOrder,
        StudentPlayerAiMessageResponse hint,
        List<StudentPlayerOptionResponse> options
) {
}
