package com.boom.parentdashboard.api;

import com.boom.parentdashboard.api.dto.DashboardPeriodPreset;
import com.boom.parentdashboard.api.dto.ParentDashboardResponse;
import com.boom.parentdashboard.application.DashboardPeriod;
import com.boom.parentdashboard.application.ParentDashboardMockService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/parents/dashboard")
public class ParentDashboardController {

    private final ParentDashboardMockService service;

    public ParentDashboardController(ParentDashboardMockService service) {
        this.service = service;
    }

    @GetMapping
    public ParentDashboardResponse getDashboard(
            @RequestHeader(value = "X-Boom-Locale", required = false) String boomLocale,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage,
            @RequestParam(value = "periodPreset", required = false, defaultValue = "LAST_30_DAYS") DashboardPeriodPreset periodPreset,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        String locale = boomLocale != null && !boomLocale.isBlank() ? boomLocale : acceptLanguage;
        DashboardPeriod period = DashboardPeriod.resolve(periodPreset, startDate, endDate);
        return service.getDashboard(locale, period);
    }
}
