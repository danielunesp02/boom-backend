package com.boom.student.player.api.dto;

public record StudentPlayerStudentResponse(
        String studentId,
        String displayName,
        String gradeLevel,
        String targetSchoolSystem,
        String preferredLocale
) {
}
