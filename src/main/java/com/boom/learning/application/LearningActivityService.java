package com.boom.learning.application;

import com.boom.learning.api.dto.ActivityQuestionResponse;
import com.boom.learning.api.dto.LearningActivityDetailResponse;
import com.boom.learning.api.dto.LearningActivitySummaryResponse;
import com.boom.learning.api.dto.QuestionOptionResponse;
import com.boom.learning.domain.ActivityQuestion;
import com.boom.learning.domain.LearningActivity;
import com.boom.learning.domain.QuestionOption;
import com.boom.learning.repository.LearningActivityRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class LearningActivityService {

    private final LearningActivityRepository repository;

    public LearningActivityService(LearningActivityRepository repository) {
        this.repository = repository;
    }

    public List<LearningActivitySummaryResponse> listActivities(UUID subjectId, UUID topicId, UUID skillId, UUID objectiveId) {
        return repository.findActiveActivities(subjectId, topicId, skillId, objectiveId).stream()
                .map(this::toSummary)
                .toList();
    }

    public LearningActivityDetailResponse getActivity(UUID activityId) {
        LearningActivity activity = repository.findActiveActivityById(activityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Learning activity not found."));

        List<ActivityQuestionResponse> questions = repository.findActiveQuestionsByActivity(activityId).stream()
                .map(this::toQuestion)
                .toList();

        return new LearningActivityDetailResponse(
                activity.id().toString(),
                activity.code(),
                activity.title(),
                activity.description(),
                activity.subjectId().toString(),
                activity.topicId().toString(),
                activity.skillId().toString(),
                nullable(activity.objectiveId()),
                nullable(activity.curriculumFrameworkId()),
                nullable(activity.curriculumBandId()),
                nullable(activity.curriculumExpectationId()),
                activity.activityType().name(),
                activity.estimatedDurationMinutes(),
                activity.complexityLevel().name(),
                activity.depthLevel().name(),
                activity.displayOrder(),
                activity.status().name(),
                questions
        );
    }

    private LearningActivitySummaryResponse toSummary(LearningActivity activity) {
        return new LearningActivitySummaryResponse(
                activity.id().toString(),
                activity.code(),
                activity.title(),
                activity.description(),
                activity.subjectId().toString(),
                activity.topicId().toString(),
                activity.skillId().toString(),
                nullable(activity.objectiveId()),
                nullable(activity.curriculumFrameworkId()),
                nullable(activity.curriculumBandId()),
                nullable(activity.curriculumExpectationId()),
                activity.activityType().name(),
                activity.estimatedDurationMinutes(),
                activity.complexityLevel().name(),
                activity.depthLevel().name(),
                activity.displayOrder(),
                activity.status().name()
        );
    }

    private ActivityQuestionResponse toQuestion(ActivityQuestion question) {
        List<QuestionOptionResponse> options = repository.findActiveOptionsByQuestion(question.id()).stream()
                .map(this::toOption)
                .toList();

        return new ActivityQuestionResponse(
                question.id().toString(),
                question.activityId().toString(),
                question.code(),
                question.prompt(),
                question.explanation(),
                question.questionType().name(),
                question.subjectId().toString(),
                question.topicId().toString(),
                question.skillId().toString(),
                nullable(question.objectiveId()),
                question.complexityLevel().name(),
                question.depthLevel().name(),
                question.displayOrder(),
                question.status().name(),
                options
        );
    }

    private QuestionOptionResponse toOption(QuestionOption option) {
        return new QuestionOptionResponse(
                option.id().toString(),
                option.questionId().toString(),
                option.label(),
                option.text(),
                option.displayOrder(),
                option.status().name()
        );
    }

    private String nullable(UUID id) {
        return id == null ? null : id.toString();
    }
}
