package com.boom.student.api.dto;

import java.util.UUID;

public record ParentStudentResponse(
        UUID studentId,
        String displayName,
        String gradeLevel,
        String targetSchoolSystem,
        String preferredLocale,
        String relationshipType,
        boolean primary
) {}
