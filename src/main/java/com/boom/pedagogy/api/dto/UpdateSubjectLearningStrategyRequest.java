package com.boom.pedagogy.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateSubjectLearningStrategyRequest(
        UUID subjectId,
        String methodologyCode,
        BigDecimal initialAssessmentScore,
        Integer weeklyStudyMinutesGoal,
        LocalDate curveStartDate,
        LocalDate targetDate
) {
}
