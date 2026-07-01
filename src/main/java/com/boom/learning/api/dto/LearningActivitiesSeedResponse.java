package com.boom.learning.api.dto;

import java.util.Map;
import java.util.UUID;

public record LearningActivitiesSeedResponse(
        int activities,
        int questions,
        int options,
        Map<String, UUID> references
) {
}
