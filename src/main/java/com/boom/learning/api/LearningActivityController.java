package com.boom.learning.api;

import com.boom.learning.api.dto.LearningActivityDetailResponse;
import com.boom.learning.api.dto.LearningActivitySummaryResponse;
import com.boom.learning.application.LearningActivityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/learning/activities")
public class LearningActivityController {

    private final LearningActivityService service;

    public LearningActivityController(LearningActivityService service) {
        this.service = service;
    }

    @GetMapping
    public List<LearningActivitySummaryResponse> listActivities(
            @RequestParam(value = "subjectId", required = false) UUID subjectId,
            @RequestParam(value = "topicId", required = false) UUID topicId,
            @RequestParam(value = "skillId", required = false) UUID skillId,
            @RequestParam(value = "objectiveId", required = false) UUID objectiveId
    ) {
        return service.listActivities(subjectId, topicId, skillId, objectiveId);
    }

    @GetMapping("/{activityId}")
    public LearningActivityDetailResponse getActivity(
            @PathVariable("activityId") UUID activityId
    ) {
        return service.getActivity(activityId);
    }
}