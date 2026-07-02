package com.boom.student.player.api.dto;

import java.util.List;

public record StudentActivityPlayerResponse(
        StudentPlayerStudentResponse student,
        StudentPlayerActivityResponse activity,
        StudentPlayerAiCoachResponse aiCoach,
        List<StudentPlayerQuestionResponse> questions
) {
}
