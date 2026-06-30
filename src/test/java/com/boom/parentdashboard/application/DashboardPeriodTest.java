package com.boom.parentdashboard.application;

import com.boom.parentdashboard.api.dto.DashboardPeriodPreset;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DashboardPeriodTest {

    @Test
    void shouldResolveCustomRangeAndComparisonPeriod() {
        DashboardPeriod period = DashboardPeriod.resolve(
                DashboardPeriodPreset.CUSTOM,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30)
        );

        assertEquals(DashboardPeriodPreset.CUSTOM, period.preset());
        assertEquals(LocalDate.of(2026, 6, 1), period.startDate());
        assertEquals(LocalDate.of(2026, 6, 30), period.endDate());
        assertEquals(LocalDate.of(2026, 5, 2), period.comparisonStartDate());
        assertEquals(LocalDate.of(2026, 5, 31), period.comparisonEndDate());
    }

    @Test
    void shouldFallbackToLast30DaysWhenCustomRangeIsMissing() {
        DashboardPeriod period = DashboardPeriod.resolve(DashboardPeriodPreset.CUSTOM, null, null);

        assertEquals(DashboardPeriodPreset.LAST_30_DAYS, period.preset());
    }
}
