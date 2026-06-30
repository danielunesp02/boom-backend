package com.boom.ai.coachingprofile.domain;

import java.time.Instant;
import java.util.UUID;

public final class CoachingAgentProfileDefaults {

    private CoachingAgentProfileDefaults() {
    }

    public static CoachingAgentProfile safeDefaultForStudent(UUID studentId) {
        Instant now = Instant.now();

        return new CoachingAgentProfile(
                UUID.randomUUID(),
                studentId,
                "en-US",
                "International",
                "International Lower Secondary",
                TeachingMethod.ADAPTIVE_DAILY_PRACTICE,
                LearningStrategy.REMEDIATION_FIRST,
                CommunicationTone.ENCOURAGING,
                ExplanationDepth.STEP_BY_STEP,
                AiProvider.OPENAI,
                "configured-by-environment",
                0.2,
                1200,
                HallucinationPolicy.STRICT,
                GroundingPolicy.USE_PLATFORM_DATA_AND_CURRICULUM,
                SafetyPolicy.EDUCATION_SAFE,
                FallbackStrategy.RULE_BASED_SUMMARY,
                CoachingAgentProfileStatus.ACTIVE,
                1,
                now,
                now,
                now,
                null
        );
    }
}
