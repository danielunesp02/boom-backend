package com.boom.parentdashboard.api;

import com.boom.parentdashboard.api.dto.DashboardPeriodPreset;
import com.boom.parentdashboard.api.dto.ParentDashboardResponse;
import com.boom.parentdashboard.application.DashboardPeriod;
import com.boom.parentdashboard.application.ParentDashboardMockService;
import com.boom.student.application.AuthenticatedGuardianResolver;
import com.boom.student.application.StudentAccessService;
import com.boom.student.domain.Student;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/parents/dashboard")
public class ParentDashboardController {

    private final ParentDashboardMockService service;
    private final AuthenticatedGuardianResolver guardianResolver;
    private final StudentAccessService studentAccessService;

    public ParentDashboardController(
            ParentDashboardMockService service,
            AuthenticatedGuardianResolver guardianResolver,
            StudentAccessService studentAccessService
    ) {
        this.service = service;
        this.guardianResolver = guardianResolver;
        this.studentAccessService = studentAccessService;
    }

    @GetMapping
    public ParentDashboardResponse getDashboard(
            Authentication authentication,
            @RequestHeader(value = "X-Boom-Locale", required = false) String boomLocale,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage,
            @RequestParam(value = "periodPreset", required = false, defaultValue = "LAST_30_DAYS") DashboardPeriodPreset periodPreset,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        UUID guardianId = guardianResolver.requireGuardianId(authentication);
        Student primaryStudent = studentAccessService.getPrimaryStudentForGuardian(guardianId);

        String locale = boomLocale != null && !boomLocale.isBlank() ? boomLocale : acceptLanguage;
        DashboardPeriod period = DashboardPeriod.resolve(periodPreset, startDate, endDate);

        return service.getDashboard(locale, period, primaryStudent);
    }
}
