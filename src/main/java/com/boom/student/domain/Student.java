package com.boom.student.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record Student(
        UUID id,
        String displayName,
        LocalDate birthDate,
        GradeLevel gradeLevel,
        TargetSchoolSystem targetSchoolSystem,
        String preferredLocale,
        StudentStatus status,
        Instant createdAt,
        Instant updatedAt
) {}
