package com.boom.student.domain;

import java.time.Instant;
import java.util.UUID;

public record GuardianStudentRelationship(
        UUID id,
        UUID guardianId,
        UUID studentId,
        RelationshipType relationshipType,
        boolean primary,
        GuardianStudentRelationshipStatus status,
        Instant createdAt,
        Instant updatedAt
) {}
