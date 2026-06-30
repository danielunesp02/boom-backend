package com.boom.student.api.dto;

import java.util.UUID;

public record DevSeedStudentResponse(
        UUID studentId,
        String displayName,
        String relationshipType,
        boolean primary,
        String message
) {}
