package com.boom.pedagogy.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record SubjectLearningStrategyResponse(
        UUID studentId,
        UUID subjectId,
        String subjectName,
        String methodologyCode,
        String calibrationStatus,
        BigDecimal initialAssessmentScore,
        BigDecimal currentMasteryScore,
        BigDecimal expectedMasteryScore,
        BigDecimal highPerformanceTargetScore,
        Integer weeklyStudyMinutesGoal,
        LocalDate curveStartDate,
        LocalDate targetDate,
        List<LearningCurvePointResponse> curvePoints
) {
}
