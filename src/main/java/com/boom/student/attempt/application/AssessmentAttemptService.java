package com.boom.student.attempt.application;
import com.boom.pedagogy.application.PedagogicalMasteryUpdateService;
import com.boom.student.snapshot.application.StudentSkillDailySnapshotService;
import com.boom.student.attempt.api.dto.*;
import com.boom.student.attempt.domain.AnswerSubmission;
import com.boom.student.attempt.domain.AssessmentAttempt;
import com.boom.student.attempt.domain.AssessmentAttemptStatus;
import com.boom.student.attempt.domain.SourceChannel;
import com.boom.student.attempt.repository.AssessmentAttemptRepository;
import com.boom.student.learningevent.application.StudentLearningEventCommand;
import com.boom.student.learningevent.application.StudentLearningEventService;
import com.boom.student.learningevent.domain.StudentLearningEventType;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AssessmentAttemptService {

    private final JdbcClient jdbcClient;
    private final AssessmentAttemptRepository repository;
    private final MockAnswerFeedbackService feedbackService;
    private final StudentLearningEventService learningEventService;
    private final StudentSkillDailySnapshotService snapshotService;
    private final PedagogicalMasteryUpdateService pedagogicalMasteryUpdateService;

    public AssessmentAttemptService(
            JdbcClient jdbcClient,
            AssessmentAttemptRepository repository,
            MockAnswerFeedbackService feedbackService,
            StudentLearningEventService learningEventService,
            StudentSkillDailySnapshotService snapshotService,
            PedagogicalMasteryUpdateService pedagogicalMasteryUpdateService
    ) {
        this.jdbcClient = jdbcClient;
        this.repository = repository;
        this.feedbackService = feedbackService;
        this.learningEventService = learningEventService;
        this.snapshotService = snapshotService;
        this.pedagogicalMasteryUpdateService = pedagogicalMasteryUpdateService;

    }

    public AssessmentAttemptResponse startAttempt(
            String studentIdValue,
            String activityIdValue,
            StartAttemptRequest request,
            Authentication authentication
    ) {
        UUID studentId = parseUuid(studentIdValue, "Invalid studentId.");
        UUID activityId = parseUuid(activityIdValue, "Invalid activityId.");
        UUID guardianId = authenticatedGuardianId(authentication);

        ensureGuardianCanAccessStudent(guardianId, studentId);
        ensureActivityExists(activityId);

        int totalQuestions = countActiveQuestions(activityId);
        SourceChannel sourceChannel = parseSourceChannel(request == null ? null : request.sourceChannel());
        String locale = request == null || request.locale() == null || request.locale().isBlank()
                ? "en-US"
                : request.locale();

        AssessmentAttempt attempt = repository.createAttempt(
                studentId,
                guardianId,
                activityId,
                sourceChannel,
                locale,
                totalQuestions
        );

        learningEventService.append(new StudentLearningEventCommand(
                studentId,
                guardianId,
                attempt.id(),
                null,
                activityId,
                null,
                StudentLearningEventType.ACTIVITY_STARTED,
                null,
                null,
                BigDecimal.ZERO,
                null,
                null,
                sourceChannel.name(),
                locale,
                "{\"source\":\"assessment_attempt_started\"}"
        ));

        return toAttemptResponse(attempt);
    }

    public AnswerSubmissionResponse submitAnswer(
            String attemptIdValue,
            SubmitAnswerRequest request,
            Authentication authentication
    ) {
        UUID attemptId = parseUuid(attemptIdValue, "Invalid attemptId.");

        if (request == null || request.questionId() == null || request.questionId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "questionId is required.");
        }

        UUID questionId = parseUuid(request.questionId(), "Invalid questionId.");
        UUID selectedOptionId = request.selectedOptionId() == null || request.selectedOptionId().isBlank()
                ? null
                : parseUuid(request.selectedOptionId(), "Invalid selectedOptionId.");

        UUID guardianId = authenticatedGuardianId(authentication);
        AssessmentAttempt attempt = loadAttempt(attemptId);
        ensureAttemptAccess(attempt, guardianId);

        if (attempt.status() != AssessmentAttemptStatus.IN_PROGRESS) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Attempt is not in progress.");
        }

        QuestionValidation validation = validateQuestionAndAnswer(attempt.activityId(), questionId, selectedOptionId);

        AnswerSubmission answer = repository.upsertAnswer(
                attemptId,
                questionId,
                selectedOptionId,
                request.textAnswer(),
                validation.correct(),
                validation.correct() ? BigDecimal.ONE : BigDecimal.ZERO,
                request.timeSpentSeconds()
        );

        repository.refreshAttemptAggregates(attemptId, false);

        learningEventService.append(new StudentLearningEventCommand(
                attempt.studentId(),
                guardianId,
                attempt.id(),
                answer.id(),
                attempt.activityId(),
                questionId,
                StudentLearningEventType.QUESTION_ANSWERED,
                null,
                validation.correct(),
                validation.correct() ? BigDecimal.ONE : BigDecimal.ZERO,
                request.timeSpentSeconds(),
                null,
                attempt.sourceChannel().name(),
                attempt.locale(),
                "{\"source\":\"answer_submitted\"}"
        ));

        return toAnswerResponse(answer, feedbackService.feedback(validation.correct(), validation.explanation()));
    }

    public AssessmentAttemptResponse completeAttempt(String attemptIdValue, Authentication authentication) {
        UUID attemptId = parseUuid(attemptIdValue, "Invalid attemptId.");
        UUID guardianId = authenticatedGuardianId(authentication);
        AssessmentAttempt attempt = loadAttempt(attemptId);
        ensureAttemptAccess(attempt, guardianId);

        if (attempt.status() == AssessmentAttemptStatus.COMPLETED) {
            return toAttemptResponse(attempt);
        }

        repository.refreshAttemptAggregates(attemptId, true);
        AssessmentAttempt completed = loadAttempt(attemptId);

        learningEventService.append(new StudentLearningEventCommand(
                completed.studentId(),
                guardianId,
                completed.id(),
                null,
                completed.activityId(),
                null,
                StudentLearningEventType.ACTIVITY_COMPLETED,
                null,
                null,
                completed.score(),
                null,
                null,
                completed.sourceChannel().name(),
                completed.locale(),
                "{\"source\":\"assessment_attempt_completed\"}"
        ));
        snapshotService.rebuildDailySnapshots(null);
        pedagogicalMasteryUpdateService.updateAfterAttemptCompleted(completed);

        return toAttemptResponse(completed);
    }

    public AssessmentAttemptResponse getAttempt(String attemptIdValue, Authentication authentication) {
        UUID attemptId = parseUuid(attemptIdValue, "Invalid attemptId.");
        UUID guardianId = authenticatedGuardianId(authentication);
        AssessmentAttempt attempt = loadAttempt(attemptId);
        ensureAttemptAccess(attempt, guardianId);

        return toAttemptResponse(attempt);
    }

    private AssessmentAttemptResponse toAttemptResponse(AssessmentAttempt attempt) {
        List<AnswerSubmissionResponse> answers = repository.findAnswersByAttempt(attempt.id()).stream()
                .map(answer -> toAnswerResponse(answer, null))
                .toList();

        return new AssessmentAttemptResponse(
                attempt.id().toString(),
                attempt.studentId().toString(),
                attempt.activityId().toString(),
                attempt.status().name(),
                attempt.sourceChannel().name(),
                attempt.locale(),
                attempt.totalQuestions(),
                attempt.answeredQuestions(),
                attempt.correctAnswers(),
                decimal(attempt.score()),
                decimal(attempt.accuracy()),
                attempt.startedAt().toString(),
                attempt.completedAt() == null ? null : attempt.completedAt().toString(),
                answers
        );
    }

    private AnswerSubmissionResponse toAnswerResponse(AnswerSubmission answer, AiCoachFeedbackResponse feedback) {
        return new AnswerSubmissionResponse(
                answer.id().toString(),
                answer.attemptId().toString(),
                answer.questionId().toString(),
                answer.selectedOptionId() == null ? null : answer.selectedOptionId().toString(),
                answer.textAnswer(),
                answer.correct(),
                decimal(answer.score()),
                answer.timeSpentSeconds(),
                answer.submittedAt().toString(),
                feedback
        );
    }

    private AssessmentAttempt loadAttempt(UUID attemptId) {
        return repository.findAttemptById(attemptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attempt not found."));
    }

    private void ensureAttemptAccess(AssessmentAttempt attempt, UUID guardianId) {
        if (!attempt.guardianId().equals(guardianId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Guardian cannot access this attempt.");
        }

        ensureGuardianCanAccessStudent(guardianId, attempt.studentId());
    }

    private void ensureActivityExists(UUID activityId) {
        Integer count = jdbcClient.sql("""
                        SELECT COUNT(*)
                        FROM learning_activities
                        WHERE id = :activityId
                          AND status = 'ACTIVE'
                        """)
                .param("activityId", activityId)
                .query(Integer.class)
                .single();

        if (count == null || count == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Learning activity not found.");
        }
    }

    private int countActiveQuestions(UUID activityId) {
        Integer count = jdbcClient.sql("""
                        SELECT COUNT(*)
                        FROM activity_questions
                        WHERE activity_id = :activityId
                          AND status = 'ACTIVE'
                        """)
                .param("activityId", activityId)
                .query(Integer.class)
                .single();

        return count == null ? 0 : count;
    }

    private QuestionValidation validateQuestionAndAnswer(UUID activityId, UUID questionId, UUID selectedOptionId) {
        QuestionValidation validation;

        if (selectedOptionId == null) {
            validation = jdbcClient.sql("""
                            SELECT aq.explanation AS explanation,
                                   FALSE AS correct
                            FROM activity_questions aq
                            WHERE aq.id = :questionId
                              AND aq.activity_id = :activityId
                              AND aq.status = 'ACTIVE'
                            """)
                    .param("questionId", questionId)
                    .param("activityId", activityId)
                    .query((rs, rowNum) -> new QuestionValidation(
                            false,
                            rs.getString("explanation")
                    ))
                    .optional()
                    .orElse(null);
        } else {
            validation = jdbcClient.sql("""
                            SELECT qo.is_correct AS correct,
                                   aq.explanation AS explanation
                            FROM activity_questions aq
                            JOIN question_options qo ON qo.question_id = aq.id
                            WHERE aq.id = :questionId
                              AND aq.activity_id = :activityId
                              AND aq.status = 'ACTIVE'
                              AND qo.id = :selectedOptionId
                              AND qo.status = 'ACTIVE'
                            """)
                    .param("questionId", questionId)
                    .param("activityId", activityId)
                    .param("selectedOptionId", selectedOptionId)
                    .query((rs, rowNum) -> new QuestionValidation(
                            rs.getBoolean("correct"),
                            rs.getString("explanation")
                    ))
                    .optional()
                    .orElse(null);
        }

        if (validation == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Question or selected option does not belong to this attempt activity.");
        }

        return validation;
    }

    private void ensureGuardianCanAccessStudent(UUID guardianId, UUID studentId) {
        Integer count = jdbcClient.sql("""
                        SELECT COUNT(*)
                        FROM guardian_student_relationships
                        WHERE guardian_id = :guardianId
                          AND student_id = :studentId
                        """)
                .param("guardianId", guardianId)
                .param("studentId", studentId)
                .query(Integer.class)
                .single();

        if (count == null || count == 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Guardian cannot access this student.");
        }
    }

    private UUID authenticatedGuardianId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required.");
        }

        Object principal = authentication.getPrincipal();

        try {
            Method method = principal.getClass().getMethod("guardianAccountId");
            Object value = method.invoke(principal);

            if (value instanceof UUID guardianId) {
                return guardianId;
            }

            if (value instanceof String guardianIdValue) {
                return UUID.fromString(guardianIdValue);
            }
        } catch (Exception ignored) {
            // Fall through to unauthorized.
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated guardian not found.");
    }

    private SourceChannel parseSourceChannel(String value) {
        if (value == null || value.isBlank()) {
            return SourceChannel.WEB;
        }

        try {
            return SourceChannel.valueOf(value);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sourceChannel.");
        }
    }

    private UUID parseUuid(String value, String message) {
        try {
            return UUID.fromString(value);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private double decimal(BigDecimal value) {
        return value == null ? 0.0 : value.doubleValue();
    }

    private record QuestionValidation(
            boolean correct,
            String explanation
    ) {
    }
}
