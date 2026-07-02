package com.boom.student.player.application;

import com.boom.learning.domain.ActivityQuestion;
import com.boom.learning.domain.LearningActivity;
import com.boom.learning.domain.QuestionOption;
import com.boom.learning.repository.LearningActivityRepository;
import com.boom.student.player.api.dto.StudentActivityPlayerResponse;
import com.boom.student.player.api.dto.StudentPlayerActivityResponse;
import com.boom.student.player.api.dto.StudentPlayerAiCoachResponse;
import com.boom.student.player.api.dto.StudentPlayerOptionResponse;
import com.boom.student.player.api.dto.StudentPlayerQuestionResponse;
import com.boom.student.player.api.dto.StudentPlayerStudentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

@Service
public class StudentActivityPlayerService {

    private final JdbcClient jdbcClient;
    private final LearningActivityRepository learningActivityRepository;
    private final AiCoachService aiCoachService;

    public StudentActivityPlayerService(
            JdbcClient jdbcClient,
            LearningActivityRepository learningActivityRepository,
            AiCoachService aiCoachService
    ) {
        this.jdbcClient = jdbcClient;
        this.learningActivityRepository = learningActivityRepository;
        this.aiCoachService = aiCoachService;
    }

    public StudentActivityPlayerResponse getPlayer(
            String studentIdValue,
            String activityIdValue,
            Authentication authentication
    ) {
        UUID studentId = parseUuid(studentIdValue, "Invalid studentId.");
        UUID activityId = parseUuid(activityIdValue, "Invalid activityId.");
        UUID guardianId = authenticatedGuardianId(authentication);

        ensureGuardianCanAccessStudent(guardianId, studentId);

        StudentProjection student = loadStudent(studentId);
        LearningActivity activity = learningActivityRepository.findActiveActivityById(activityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Learning activity not found."));

        List<ActivityQuestion> questions = learningActivityRepository.findActiveQuestionsByActivity(activityId);

        List<StudentPlayerQuestionResponse> questionResponses = questions.stream()
                .map(question -> toQuestionResponse(activity, question))
                .toList();

        return new StudentActivityPlayerResponse(
                new StudentPlayerStudentResponse(
                        student.studentId().toString(),
                        student.displayName(),
                        student.gradeLevel(),
                        student.targetSchoolSystem(),
                        student.preferredLocale()
                ),
                new StudentPlayerActivityResponse(
                        activity.id().toString(),
                        activity.code(),
                        activity.title(),
                        activity.description(),
                        activity.subjectId().toString(),
                        activity.topicId().toString(),
                        activity.skillId().toString(),
                        nullable(activity.objectiveId()),
                        activity.activityType().name(),
                        activity.estimatedDurationMinutes(),
                        activity.complexityLevel().name(),
                        activity.depthLevel().name()
                ),
                new StudentPlayerAiCoachResponse(
                        "Boom Coach",
                        "boom-coach-01",
                        aiCoachService.activityIntro(activity, student.displayName()),
                        aiCoachService.completionPreview(activity, student.displayName())
                ),
                questionResponses
        );
    }

    private StudentPlayerQuestionResponse toQuestionResponse(LearningActivity activity, ActivityQuestion question) {
        List<QuestionOption> options = learningActivityRepository.findActiveOptionsByQuestion(question.id());

        return new StudentPlayerQuestionResponse(
                question.id().toString(),
                question.code(),
                question.prompt(),
                question.questionType().name(),
                question.complexityLevel().name(),
                question.depthLevel().name(),
                question.displayOrder(),
                aiCoachService.questionHint(activity, question),
                options.stream()
                        .map(option -> new StudentPlayerOptionResponse(
                                option.id().toString(),
                                option.label(),
                                option.text(),
                                option.displayOrder()
                        ))
                        .toList()
        );
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

    private StudentProjection loadStudent(UUID studentId) {
        return jdbcClient.sql("""
                        SELECT id, display_name, grade_level, target_school_system, preferred_locale
                        FROM students
                        WHERE id = :studentId
                        """)
                .param("studentId", studentId)
                .query((rs, rowNum) -> new StudentProjection(
                        rs.getObject("id", UUID.class),
                        rs.getString("display_name"),
                        rs.getString("grade_level"),
                        rs.getString("target_school_system"),
                        rs.getString("preferred_locale")
                ))
                .optional()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found."));
    }

    private UUID parseUuid(String value, String message) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private String nullable(UUID value) {
        return value == null ? null : value.toString();
    }

    private record StudentProjection(
            UUID studentId,
            String displayName,
            String gradeLevel,
            String targetSchoolSystem,
            String preferredLocale
    ) {
    }
}
