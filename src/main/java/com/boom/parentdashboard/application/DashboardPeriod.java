package com.boom.parentdashboard.application;

import com.boom.parentdashboard.api.dto.DashboardPeriodPreset;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record DashboardPeriod(
        DashboardPeriodPreset preset,
        LocalDate startDate,
        LocalDate endDate,
        LocalDate comparisonStartDate,
        LocalDate comparisonEndDate
) {

    public static DashboardPeriod resolve(DashboardPeriodPreset preset, LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();
        DashboardPeriodPreset resolvedPreset = preset == null ? DashboardPeriodPreset.LAST_30_DAYS : preset;

        LocalDate resolvedStart;
        LocalDate resolvedEnd;

        switch (resolvedPreset) {
            case LAST_7_DAYS -> {
                resolvedEnd = today;
                resolvedStart = today.minusDays(6);
            }
            case LAST_90_DAYS -> {
                resolvedEnd = today;
                resolvedStart = today.minusDays(89);
            }
            case CURRENT_MONTH -> {
                resolvedEnd = today;
                resolvedStart = today.withDayOfMonth(1);
            }
            case CUSTOM -> {
                if (startDate == null || endDate == null) {
                    resolvedPreset = DashboardPeriodPreset.LAST_30_DAYS;
                    resolvedEnd = today;
                    resolvedStart = today.minusDays(29);
                } else {
                    resolvedStart = startDate;
                    resolvedEnd = endDate;
                }
            }
            case LAST_30_DAYS -> {
                resolvedEnd = today;
                resolvedStart = today.minusDays(29);
            }
            default -> {
                resolvedEnd = today;
                resolvedStart = today.minusDays(29);
            }
        }

        if (resolvedStart.isAfter(resolvedEnd)) {
            LocalDate temporary = resolvedStart;
            resolvedStart = resolvedEnd;
            resolvedEnd = temporary;
        }

        long days = ChronoUnit.DAYS.between(resolvedStart, resolvedEnd) + 1;
        LocalDate comparisonEnd = resolvedStart.minusDays(1);
        LocalDate comparisonStart = comparisonEnd.minusDays(days - 1);

        return new DashboardPeriod(resolvedPreset, resolvedStart, resolvedEnd, comparisonStart, comparisonEnd);
    }
}
