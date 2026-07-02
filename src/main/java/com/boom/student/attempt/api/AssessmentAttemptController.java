package com.boom.student.attempt.api;

import com.boom.student.attempt.api.dto.AssessmentAttemptResponse;
import com.boom.student.attempt.api.dto.StartAttemptRequest;
import com.boom.student.attempt.api.dto.SubmitAnswerRequest;
import com.boom.student.attempt.api.dto.AnswerSubmissionResponse;
import com.boom.student.attempt.application.AssessmentAttemptService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AssessmentAttemptController {

    private final AssessmentAttemptService service;

    public AssessmentAttemptController(AssessmentAttemptService service) {
        this.service = service;
    }

    @PostMapping("/students/{studentId}/activities/{activityId}/attempts")
    @ResponseStatus(HttpStatus.CREATED)
    public AssessmentAttemptResponse startAttempt(
            @PathVariable("studentId") String studentId,
            @PathVariable("activityId") String activityId,
            @RequestBody(required = false) StartAttemptRequest request,
            Authentication authentication
    ) {
        return service.startAttempt(studentId, activityId, request, authentication);
    }

    @PostMapping("/attempts/{attemptId}/answers")
    @ResponseStatus(HttpStatus.CREATED)
    public AnswerSubmissionResponse submitAnswer(
            @PathVariable("attemptId") String attemptId,
            @RequestBody SubmitAnswerRequest request,
            Authentication authentication
    ) {
        return service.submitAnswer(attemptId, request, authentication);
    }

    @PostMapping("/attempts/{attemptId}/complete")
    public AssessmentAttemptResponse completeAttempt(
            @PathVariable("attemptId") String attemptId,
            Authentication authentication
    ) {
        return service.completeAttempt(attemptId, authentication);
    }

    @GetMapping("/attempts/{attemptId}")
    public AssessmentAttemptResponse getAttempt(
            @PathVariable("attemptId") String attemptId,
            Authentication authentication
    ) {
        return service.getAttempt(attemptId, authentication);
    }
}
