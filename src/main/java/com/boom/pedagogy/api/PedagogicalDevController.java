package com.boom.pedagogy.api;

import com.boom.pedagogy.application.PedagogicalReviewQueueMaintenanceResult;
import com.boom.pedagogy.application.PedagogicalReviewQueueMaintenanceService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dev/pedagogy")
public class PedagogicalDevController {

    private final PedagogicalReviewQueueMaintenanceService reviewQueueMaintenanceService;

    public PedagogicalDevController(PedagogicalReviewQueueMaintenanceService reviewQueueMaintenanceService) {
        this.reviewQueueMaintenanceService = reviewQueueMaintenanceService;
    }

    @PostMapping("/review-queue/refresh")
    public PedagogicalReviewQueueMaintenanceResult refreshReviewQueue(
            @RequestParam(value = "date", required = false) String date
    ) {
        return reviewQueueMaintenanceService.refreshReviewQueue(date);
    }
}
