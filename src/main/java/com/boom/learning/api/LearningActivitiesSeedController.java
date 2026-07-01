package com.boom.learning.api;

import com.boom.learning.api.dto.LearningActivitiesSeedResponse;
import com.boom.learning.application.LearningActivitiesSeedService;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("local")
@RestController
@RequestMapping("/api/v1/dev/seed/learning-activities")
public class LearningActivitiesSeedController {

    private final LearningActivitiesSeedService seedService;

    public LearningActivitiesSeedController(LearningActivitiesSeedService seedService) {
        this.seedService = seedService;
    }

    @PostMapping
    public LearningActivitiesSeedResponse seed() {
        LearningActivitiesSeedService.SeedResult result = seedService.seed();

        return new LearningActivitiesSeedResponse(
                result.activities(),
                result.questions(),
                result.options(),
                result.references()
        );
    }
}
