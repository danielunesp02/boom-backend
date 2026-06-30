package com.boom.ai.coachingprofile.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record CoachingAgentProfile(
        UUID id,
        UUID studentId,
        String locale,
        String targetCountry,
        String targetSchoolSystem,
        TeachingMethod teachingMethod,
        LearningStrategy learningStrategy,
        CommunicationTone communicationTone,
        ExplanationDepth explanationDepth,
        AiProvider aiProvider,
        String aiModel,
        double temperature,
        Integer maxTokens,
        HallucinationPolicy hallucinationPolicy,
        GroundingPolicy groundingPolicy,
        SafetyPolicy safetyPolicy,
        FallbackStrategy fallbackStrategy,
        CoachingAgentProfileStatus status,
        int version,
        Instant createdAt,
        Instant updatedAt,
        Instant activatedAt,
        Instant replacedAt
) {

    public CoachingAgentProfile {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(studentId, "studentId is required");
        Objects.requireNonNull(locale, "locale is required");
        Objects.requireNonNull(teachingMethod, "teachingMethod is required");
        Objects.requireNonNull(learningStrategy, "learningStrategy is required");
        Objects.requireNonNull(communicationTone, "communicationTone is required");
        Objects.requireNonNull(explanationDepth, "explanationDepth is required");
        Objects.requireNonNull(aiProvider, "aiProvider is required");
        Objects.requireNonNull(hallucinationPolicy, "hallucinationPolicy is required");
        Objects.requireNonNull(groundingPolicy, "groundingPolicy is required");
        Objects.requireNonNull(safetyPolicy, "safetyPolicy is required");
        Objects.requireNonNull(fallbackStrategy, "fallbackStrategy is required");
        Objects.requireNonNull(status, "status is required");
        Objects.requireNonNull(createdAt, "createdAt is required");
        Objects.requireNonNull(updatedAt, "updatedAt is required");

        if (version < 1) {
            throw new IllegalArgumentException("version must be greater than or equal to 1");
        }

        if (temperature < 0.0 || temperature > 2.0) {
            throw new IllegalArgumentException("temperature must be between 0.0 and 2.0");
        }
    }

    public boolean isActive() {
        return status == CoachingAgentProfileStatus.ACTIVE;
    }

    public boolean requiresStrictGrounding() {
        return hallucinationPolicy == HallucinationPolicy.STRICT
                || groundingPolicy == GroundingPolicy.USE_ONLY_PLATFORM_DATA
                || groundingPolicy == GroundingPolicy.USE_PLATFORM_DATA_AND_CURRICULUM
                || groundingPolicy == GroundingPolicy.REQUIRE_SOURCE_REFERENCE;
    }
}
