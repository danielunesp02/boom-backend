package com.boom.parentdashboard.api;

import com.boom.parentdashboard.api.dto.ParentDashboardResponse;
import com.boom.parentdashboard.application.ParentDashboardMockService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/parents")
public class ParentDashboardController {

    private final ParentDashboardMockService parentDashboardMockService;

    public ParentDashboardController(ParentDashboardMockService parentDashboardMockService) {
        this.parentDashboardMockService = parentDashboardMockService;
    }

    @GetMapping("/dashboard")
    public ParentDashboardResponse dashboard(
            @RequestHeader(name = "X-Boom-Locale", required = false) String boomLocale,
            @RequestHeader(name = "Accept-Language", required = false) String acceptLanguage
    ) {
        return parentDashboardMockService.getDashboard(boomLocale, acceptLanguage);
    }
}
