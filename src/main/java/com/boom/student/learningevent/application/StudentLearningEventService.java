package com.boom.student.learningevent.application;

import com.boom.student.learningevent.domain.StudentLearningEvent;
import com.boom.student.learningevent.repository.StudentLearningEventRepository;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.UUID;

@Service
public class StudentLearningEventService {

    private final JdbcClient jdbcClient;
    private final StudentLearningEventRepository repository;

    public StudentLearningEventService(
            JdbcClient jdbcClient,
            StudentLearningEventRepository repository
    ) {
        this.jdbcClient = jdbcClient;
        this.repository = repository;
    }

    public void append(StudentLearningEventCommand command) {
        Instant now = Instant.now();
        LocalDate eventDate = LocalDate.ofInstant(now, ZoneOffset.UTC);
        int eventWeek = eventDate.get(WeekFields.of(Locale.ROOT).weekOfWeekBasedYear());

        LearningContext context = loadLearningContext(command.activityId(), command.questionId());
        StudentContext student = loadStudentContext(command.studentId());

        StudentLearningEvent event = new StudentLearningEvent(
                UUID.randomUUID(),
                command.studentId(),
                command.guardianId(),
                command.assessmentAttemptId(),
                command.answerSubmissionId(),
                context.subjectId(),
                context.topicId(),
                context.skillId(),
                context.objectiveId(),
                command.activityId(),
                command.questionId(),
                context.curriculumFrameworkId(),
                context.curriculumBandId(),
                context.curriculumExpectationId(),
                command.eventType(),
                now,
                eventDate,
                eventDate.getYear(),
                eventDate.getMonthValue(),
                eventDate.getDayOfMonth(),
                eventWeek,
                command.clientEventTime(),
                now,
                student.countryCode(),
                student.gradeLevel(),
                student.targetSchoolSystem(),
                null,
                null,
                null,
                context.complexityLevel(),
                context.depthLevel(),
                command.correct(),
                command.score(),
                command.timeSpentSeconds(),
                command.attemptNumber(),
                command.sourceChannel() == null || command.sourceChannel().isBlank() ? "WEB" : command.sourceChannel(),
                command.locale(),
                command.metadataJson(),
                now
        );

        repository.append(event);
    }

    private LearningContext loadLearningContext(UUID activityId, UUID questionId) {
        if (questionId != null) {
            return jdbcClient.sql("""
                            SELECT aq.subject_id,
                                   aq.topic_id,
                                   aq.skill_id,
                                   aq.objective_id,
                                   la.curriculum_framework_id,
                                   la.curriculum_band_id,
                                   la.curriculum_expectation_id,
                                   aq.complexity_level,
                                   aq.depth_level
                            FROM activity_questions aq
                            JOIN learning_activities la ON la.id = aq.activity_id
                            WHERE aq.id = :questionId
                              AND aq.activity_id = :activityId
                            """)
                    .param("activityId", activityId)
                    .param("questionId", questionId)
                    .query((rs, rowNum) -> new LearningContext(
                            rs.getObject("subject_id", UUID.class),
                            rs.getObject("topic_id", UUID.class),
                            rs.getObject("skill_id", UUID.class),
                            rs.getObject("objective_id", UUID.class),
                            rs.getObject("curriculum_framework_id", UUID.class),
                            rs.getObject("curriculum_band_id", UUID.class),
                            rs.getObject("curriculum_expectation_id", UUID.class),
                            rs.getString("complexity_level"),
                            rs.getString("depth_level")
                    ))
                    .optional()
                    .orElseGet(() -> loadActivityContext(activityId));
        }

        return loadActivityContext(activityId);
    }

    private LearningContext loadActivityContext(UUID activityId) {
        return jdbcClient.sql("""
                        SELECT subject_id,
                               topic_id,
                               skill_id,
                               objective_id,
                               curriculum_framework_id,
                               curriculum_band_id,
                               curriculum_expectation_id,
                               complexity_level,
                               depth_level
                        FROM learning_activities
                        WHERE id = :activityId
                        """)
                .param("activityId", activityId)
                .query((rs, rowNum) -> new LearningContext(
                        rs.getObject("subject_id", UUID.class),
                        rs.getObject("topic_id", UUID.class),
                        rs.getObject("skill_id", UUID.class),
                        rs.getObject("objective_id", UUID.class),
                        rs.getObject("curriculum_framework_id", UUID.class),
                        rs.getObject("curriculum_band_id", UUID.class),
                        rs.getObject("curriculum_expectation_id", UUID.class),
                        rs.getString("complexity_level"),
                        rs.getString("depth_level")
                ))
                .single();
    }

    private StudentContext loadStudentContext(UUID studentId) {
        return jdbcClient.sql("""
                        SELECT grade_level,
                               target_school_system
                        FROM students
                        WHERE id = :studentId
                        """)
                .param("studentId", studentId)
                .query((rs, rowNum) -> new StudentContext(
                        null,
                        rs.getString("grade_level"),
                        rs.getString("target_school_system")
                ))
                .single();
    }

    private record LearningContext(
            UUID subjectId,
            UUID topicId,
            UUID skillId,
            UUID objectiveId,
            UUID curriculumFrameworkId,
            UUID curriculumBandId,
            UUID curriculumExpectationId,
            String complexityLevel,
            String depthLevel
    ) {
    }

    private record StudentContext(
            String countryCode,
            String gradeLevel,
            String targetSchoolSystem
    ) {
    }
}
