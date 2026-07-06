package com.boom.pedagogy.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LearningCurvePointResponse(
        LocalDate pointDate,
        int weekIndex,
        BigDecimal actualMasteryScore,
        BigDecimal expectedMasteryScore,
        BigDecimal highPerformanceScore
) {
}
