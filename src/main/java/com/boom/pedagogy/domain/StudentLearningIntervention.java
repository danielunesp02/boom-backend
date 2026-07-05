package com.boom.pedagogy.domain;

import java.time.Instant;
import java.util.UUID;

public record StudentLearningIntervention(
        UUID id,
        UUID studentId,
        UUID subjectId,
        UUID topicId,
        UUID skillId,
        UUID reviewQueueId,
        UUID methodologyId,
        InterventionType interventionType,
        String triggerReason,
        String inputSnapshotJson,
        String recommendationJson,
        boolean aiUsed,
        String aiProvider,
        String aiModel,
        InterventionStatus status,
        Instant createdAt,
        Instant completedAt,
        Instant updatedAt
) {
}
