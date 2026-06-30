package com.boom.ai.coachingprofile.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CoachingAgentProfileTest {

    @Test
    void shouldCreateSafeDefaultProfileForStudent() {
        UUID studentId = UUID.randomUUID();

        CoachingAgentProfile profile = CoachingAgentProfileDefaults.safeDefaultForStudent(studentId);

        assertEquals(studentId, profile.studentId());
        assertEquals("en-US", profile.locale());
        assertEquals(TeachingMethod.ADAPTIVE_DAILY_PRACTICE, profile.teachingMethod());
        assertEquals(LearningStrategy.REMEDIATION_FIRST, profile.learningStrategy());
        assertEquals(CommunicationTone.ENCOURAGING, profile.communicationTone());
        assertEquals(ExplanationDepth.STEP_BY_STEP, profile.explanationDepth());
        assertEquals(AiProvider.OPENAI, profile.aiProvider());
        assertEquals(HallucinationPolicy.STRICT, profile.hallucinationPolicy());
        assertEquals(GroundingPolicy.USE_PLATFORM_DATA_AND_CURRICULUM, profile.groundingPolicy());
        assertEquals(FallbackStrategy.RULE_BASED_SUMMARY, profile.fallbackStrategy());
        assertEquals(CoachingAgentProfileStatus.ACTIVE, profile.status());
        assertEquals(1, profile.version());
        assertTrue(profile.isActive());
        assertTrue(profile.requiresStrictGrounding());
    }

    @Test
    void shouldRejectInvalidVersion() {
        UUID studentId = UUID.randomUUID();
        CoachingAgentProfile defaultProfile = CoachingAgentProfileDefaults.safeDefaultForStudent(studentId);

        assertThrows(IllegalArgumentException.class, () -> new CoachingAgentProfile(
                defaultProfile.id(),
                defaultProfile.studentId(),
                defaultProfile.locale(),
                defaultProfile.targetCountry(),
                defaultProfile.targetSchoolSystem(),
                defaultProfile.teachingMethod(),
                defaultProfile.learningStrategy(),
                defaultProfile.communicationTone(),
                defaultProfile.explanationDepth(),
                defaultProfile.aiProvider(),
                defaultProfile.aiModel(),
                defaultProfile.temperature(),
                defaultProfile.maxTokens(),
                defaultProfile.hallucinationPolicy(),
                defaultProfile.groundingPolicy(),
                defaultProfile.safetyPolicy(),
                defaultProfile.fallbackStrategy(),
                defaultProfile.status(),
                0,
                defaultProfile.createdAt(),
                defaultProfile.updatedAt(),
                defaultProfile.activatedAt(),
                defaultProfile.replacedAt()
        ));
    }

    @Test
    void shouldRejectInvalidTemperature() {
        UUID studentId = UUID.randomUUID();
        CoachingAgentProfile defaultProfile = CoachingAgentProfileDefaults.safeDefaultForStudent(studentId);

        assertThrows(IllegalArgumentException.class, () -> new CoachingAgentProfile(
                defaultProfile.id(),
                defaultProfile.studentId(),
                defaultProfile.locale(),
                defaultProfile.targetCountry(),
                defaultProfile.targetSchoolSystem(),
                defaultProfile.teachingMethod(),
                defaultProfile.learningStrategy(),
                defaultProfile.communicationTone(),
                defaultProfile.explanationDepth(),
                defaultProfile.aiProvider(),
                defaultProfile.aiModel(),
                3.0,
                defaultProfile.maxTokens(),
                defaultProfile.hallucinationPolicy(),
                defaultProfile.groundingPolicy(),
                defaultProfile.safetyPolicy(),
                defaultProfile.fallbackStrategy(),
                defaultProfile.status(),
                defaultProfile.version(),
                defaultProfile.createdAt(),
                defaultProfile.updatedAt(),
                defaultProfile.activatedAt(),
                defaultProfile.replacedAt()
        ));
    }
}
