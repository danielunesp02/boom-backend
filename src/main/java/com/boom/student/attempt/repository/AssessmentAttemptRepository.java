package com.boom.student.attempt.repository;

import com.boom.student.attempt.domain.AnswerSubmission;
import com.boom.student.attempt.domain.AssessmentAttempt;
import com.boom.student.attempt.domain.AssessmentAttemptStatus;
import com.boom.student.attempt.domain.SourceChannel;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AssessmentAttemptRepository {

    private final JdbcClient jdbcClient;

    public AssessmentAttemptRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public AssessmentAttempt createAttempt(
            UUID studentId,
            UUID guardianId,
            UUID activityId,
            SourceChannel sourceChannel,
            String locale,
            int totalQuestions
    ) {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        jdbcClient.sql("""
                        INSERT INTO assessment_attempts (
                            id, student_id, guardian_id, activity_id, status, source_channel, locale,
                            total_questions, answered_questions, correct_answers, score, accuracy,
                            started_at, completed_at, created_at, updated_at
                        )
                        VALUES (
                            :id, :studentId, :guardianId, :activityId, 'IN_PROGRESS', :sourceChannel, :locale,
                            :totalQuestions, 0, 0, 0, 0,
                            :startedAt, NULL, :createdAt, :updatedAt
                        )
                        """)
                .param("id", id)
                .param("studentId", studentId)
                .param("guardianId", guardianId)
                .param("activityId", activityId)
                .param("sourceChannel", sourceChannel.name())
                .param("locale", locale)
                .param("totalQuestions", totalQuestions)
                .param("startedAt", Timestamp.from(now))
                .param("createdAt", Timestamp.from(now))
                .param("updatedAt", Timestamp.from(now))
                .update();

        return findAttemptById(id).orElseThrow();
    }

    public Optional<AssessmentAttempt> findAttemptById(UUID attemptId) {
        return jdbcClient.sql("""
                        SELECT id, student_id, guardian_id, activity_id, status, source_channel, locale,
                               total_questions, answered_questions, correct_answers, score, accuracy,
                               started_at, completed_at, created_at, updated_at
                        FROM assessment_attempts
                        WHERE id = :attemptId
                        """)
                .param("attemptId", attemptId)
                .query((rs, rowNum) -> new AssessmentAttempt(
                        rs.getObject("id", UUID.class),
                        rs.getObject("student_id", UUID.class),
                        rs.getObject("guardian_id", UUID.class),
                        rs.getObject("activity_id", UUID.class),
                        AssessmentAttemptStatus.valueOf(rs.getString("status")),
                        SourceChannel.valueOf(rs.getString("source_channel")),
                        rs.getString("locale"),
                        rs.getInt("total_questions"),
                        rs.getInt("answered_questions"),
                        rs.getInt("correct_answers"),
                        rs.getBigDecimal("score"),
                        rs.getBigDecimal("accuracy"),
                        rs.getTimestamp("started_at").toInstant(),
                        rs.getTimestamp("completed_at") == null ? null : rs.getTimestamp("completed_at").toInstant(),
                        rs.getTimestamp("created_at").toInstant(),
                        rs.getTimestamp("updated_at").toInstant()
                ))
                .optional();
    }

    public AnswerSubmission upsertAnswer(
            UUID attemptId,
            UUID questionId,
            UUID selectedOptionId,
            String textAnswer,
            boolean correct,
            BigDecimal score,
            Integer timeSpentSeconds
    ) {
        UUID existingId = jdbcClient.sql("""
                        SELECT id
                        FROM answer_submissions
                        WHERE attempt_id = :attemptId
                          AND question_id = :questionId
                        """)
                .param("attemptId", attemptId)
                .param("questionId", questionId)
                .query(UUID.class)
                .optional()
                .orElse(null);

        Instant now = Instant.now();

        if (existingId != null) {
            jdbcClient.sql("""
                            UPDATE answer_submissions
                            SET selected_option_id = :selectedOptionId,
                                text_answer = :textAnswer,
                                correct = :correct,
                                score = :score,
                                time_spent_seconds = :timeSpentSeconds,
                                submitted_at = :submittedAt,
                                updated_at = :updatedAt
                            WHERE id = :id
                            """)
                    .param("id", existingId)
                    .param("selectedOptionId", selectedOptionId)
                    .param("textAnswer", textAnswer)
                    .param("correct", correct)
                    .param("score", score)
                    .param("timeSpentSeconds", timeSpentSeconds)
                    .param("submittedAt", Timestamp.from(now))
                    .param("updatedAt", Timestamp.from(now))
                    .update();

            return findAnswerById(existingId).orElseThrow();
        }

        UUID id = UUID.randomUUID();

        jdbcClient.sql("""
                        INSERT INTO answer_submissions (
                            id, attempt_id, question_id, selected_option_id, text_answer,
                            correct, score, time_spent_seconds, submitted_at, created_at, updated_at
                        )
                        VALUES (
                            :id, :attemptId, :questionId, :selectedOptionId, :textAnswer,
                            :correct, :score, :timeSpentSeconds, :submittedAt, :createdAt, :updatedAt
                        )
                        """)
                .param("id", id)
                .param("attemptId", attemptId)
                .param("questionId", questionId)
                .param("selectedOptionId", selectedOptionId)
                .param("textAnswer", textAnswer)
                .param("correct", correct)
                .param("score", score)
                .param("timeSpentSeconds", timeSpentSeconds)
                .param("submittedAt", Timestamp.from(now))
                .param("createdAt", Timestamp.from(now))
                .param("updatedAt", Timestamp.from(now))
                .update();

        return findAnswerById(id).orElseThrow();
    }

    public Optional<AnswerSubmission> findAnswerById(UUID answerId) {
        return jdbcClient.sql("""
                        SELECT id, attempt_id, question_id, selected_option_id, text_answer,
                               correct, score, time_spent_seconds, submitted_at, created_at, updated_at
                        FROM answer_submissions
                        WHERE id = :answerId
                        """)
                .param("answerId", answerId)
                .query((rs, rowNum) -> new AnswerSubmission(
                        rs.getObject("id", UUID.class),
                        rs.getObject("attempt_id", UUID.class),
                        rs.getObject("question_id", UUID.class),
                        rs.getObject("selected_option_id", UUID.class),
                        rs.getString("text_answer"),
                        rs.getBoolean("correct"),
                        rs.getBigDecimal("score"),
                        rs.getObject("time_spent_seconds", Integer.class),
                        rs.getTimestamp("submitted_at").toInstant(),
                        rs.getTimestamp("created_at").toInstant(),
                        rs.getTimestamp("updated_at").toInstant()
                ))
                .optional();
    }

    public List<AnswerSubmission> findAnswersByAttempt(UUID attemptId) {
        return jdbcClient.sql("""
                        SELECT id, attempt_id, question_id, selected_option_id, text_answer,
                               correct, score, time_spent_seconds, submitted_at, created_at, updated_at
                        FROM answer_submissions
                        WHERE attempt_id = :attemptId
                        ORDER BY submitted_at
                        """)
                .param("attemptId", attemptId)
                .query((rs, rowNum) -> new AnswerSubmission(
                        rs.getObject("id", UUID.class),
                        rs.getObject("attempt_id", UUID.class),
                        rs.getObject("question_id", UUID.class),
                        rs.getObject("selected_option_id", UUID.class),
                        rs.getString("text_answer"),
                        rs.getBoolean("correct"),
                        rs.getBigDecimal("score"),
                        rs.getObject("time_spent_seconds", Integer.class),
                        rs.getTimestamp("submitted_at").toInstant(),
                        rs.getTimestamp("created_at").toInstant(),
                        rs.getTimestamp("updated_at").toInstant()
                ))
                .list();
    }

    public void refreshAttemptAggregates(UUID attemptId, boolean complete) {
        Instant now = Instant.now();

        jdbcClient.sql("""
                        UPDATE assessment_attempts
                        SET answered_questions = (
                                SELECT COUNT(*)
                                FROM answer_submissions
                                WHERE attempt_id = :attemptId
                            ),
                            correct_answers = (
                                SELECT COUNT(*)
                                FROM answer_submissions
                                WHERE attempt_id = :attemptId
                                  AND correct = TRUE
                            ),
                            score = (
                                SELECT COALESCE(SUM(score), 0)
                                FROM answer_submissions
                                WHERE attempt_id = :attemptId
                            ),
                            accuracy = CASE
                                WHEN total_questions = 0 THEN 0
                                ELSE (
                                    (
                                        SELECT COUNT(*)
                                        FROM answer_submissions
                                        WHERE attempt_id = :attemptId
                                          AND correct = TRUE
                                    )::numeric / total_questions::numeric
                                ) * 100
                            END,
                            status = CASE WHEN :complete THEN 'COMPLETED' ELSE status END,
                            completed_at = CASE WHEN :complete THEN :completedAt ELSE completed_at END,
                            updated_at = :updatedAt
                        WHERE id = :attemptId
                        """)
                .param("attemptId", attemptId)
                .param("complete", complete)
                .param("completedAt", Timestamp.from(now))
                .param("updatedAt", Timestamp.from(now))
                .update();
    }
}
