package com.boom.student.learningevent.repository;

import com.boom.student.learningevent.domain.StudentLearningEvent;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public class StudentLearningEventRepository {

    private final JdbcClient jdbcClient;

    public StudentLearningEventRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void append(StudentLearningEvent event) {
        jdbcClient.sql("""
                        INSERT INTO student_learning_events (
                            id,
                            student_id,
                            guardian_id,
                            assessment_attempt_id,
                            answer_submission_id,
                            subject_id,
                            topic_id,
                            skill_id,
                            learning_objective_id,
                            activity_id,
                            question_id,
                            curriculum_framework_id,
                            curriculum_band_id,
                            curriculum_expectation_id,
                            event_type,
                            event_time,
                            event_date,
                            event_year,
                            event_month,
                            event_day,
                            event_week,
                            client_event_time,
                            server_received_at,
                            country_code,
                            grade_level,
                            target_school_system,
                            age_at_event_months,
                            knowledge_level_before,
                            knowledge_level_after,
                            complexity_level,
                            depth_level,
                            is_correct,
                            score,
                            time_spent_seconds,
                            attempt_number,
                            source_channel,
                            locale,
                            metadata,
                            created_at
                        )
                        VALUES (
                            :id,
                            :studentId,
                            :guardianId,
                            :assessmentAttemptId,
                            :answerSubmissionId,
                            :subjectId,
                            :topicId,
                            :skillId,
                            :learningObjectiveId,
                            :activityId,
                            :questionId,
                            :curriculumFrameworkId,
                            :curriculumBandId,
                            :curriculumExpectationId,
                            :eventType,
                            :eventTime,
                            :eventDate,
                            :eventYear,
                            :eventMonth,
                            :eventDay,
                            :eventWeek,
                            :clientEventTime,
                            :serverReceivedAt,
                            :countryCode,
                            :gradeLevel,
                            :targetSchoolSystem,
                            :ageAtEventMonths,
                            :knowledgeLevelBefore,
                            :knowledgeLevelAfter,
                            :complexityLevel,
                            :depthLevel,
                            :correct,
                            :score,
                            :timeSpentSeconds,
                            :attemptNumber,
                            :sourceChannel,
                            :locale,
                            CAST(:metadataJson AS jsonb),
                            :createdAt
                        )
                        """)
                .param("id", event.id())
                .param("studentId", event.studentId())
                .param("guardianId", event.guardianId())
                .param("assessmentAttemptId", event.assessmentAttemptId())
                .param("answerSubmissionId", event.answerSubmissionId())
                .param("subjectId", event.subjectId())
                .param("topicId", event.topicId())
                .param("skillId", event.skillId())
                .param("learningObjectiveId", event.learningObjectiveId())
                .param("activityId", event.activityId())
                .param("questionId", event.questionId())
                .param("curriculumFrameworkId", event.curriculumFrameworkId())
                .param("curriculumBandId", event.curriculumBandId())
                .param("curriculumExpectationId", event.curriculumExpectationId())
                .param("eventType", event.eventType().name())
                .param("eventTime", Timestamp.from(event.eventTime()))
                .param("eventDate", event.eventDate())
                .param("eventYear", event.eventYear())
                .param("eventMonth", event.eventMonth())
                .param("eventDay", event.eventDay())
                .param("eventWeek", event.eventWeek())
                .param("clientEventTime", event.clientEventTime() == null ? null : Timestamp.from(event.clientEventTime()))
                .param("serverReceivedAt", Timestamp.from(event.serverReceivedAt()))
                .param("countryCode", event.countryCode())
                .param("gradeLevel", event.gradeLevel())
                .param("targetSchoolSystem", event.targetSchoolSystem())
                .param("ageAtEventMonths", event.ageAtEventMonths())
                .param("knowledgeLevelBefore", event.knowledgeLevelBefore())
                .param("knowledgeLevelAfter", event.knowledgeLevelAfter())
                .param("complexityLevel", event.complexityLevel())
                .param("depthLevel", event.depthLevel())
                .param("correct", event.correct())
                .param("score", event.score())
                .param("timeSpentSeconds", event.timeSpentSeconds())
                .param("attemptNumber", event.attemptNumber())
                .param("sourceChannel", event.sourceChannel())
                .param("locale", event.locale())
                .param("metadataJson", event.metadataJson() == null ? "{}" : event.metadataJson())
                .param("createdAt", Timestamp.from(event.createdAt()))
                .update();
    }
}
