package com.boom.student.snapshot.repository;

import com.boom.student.snapshot.domain.StudentSkillDailySnapshot;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public class StudentSkillDailySnapshotRepository {

    private final JdbcClient jdbcClient;

    public StudentSkillDailySnapshotRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public int rebuildForDate(LocalDate snapshotDate) {
        jdbcClient.sql("DELETE FROM student_skill_daily_snapshots WHERE snapshot_date = :snapshotDate")
                .param("snapshotDate", snapshotDate)
                .update();

        return jdbcClient.sql("""
                INSERT INTO student_skill_daily_snapshots (
                    id, snapshot_date, student_id, guardian_id, subject_id, topic_id, skill_id,
                    grade_level, target_school_system, country_code, locale,
                    questions_answered, correct_answers, incorrect_answers,
                    accuracy, total_score, average_score,
                    total_time_spent_seconds, average_time_spent_seconds,
                    activities_started, activities_completed, attempts_completed,
                    first_event_at, last_event_at, created_at, updated_at
                )
                SELECT
                    gen_random_uuid(),
                    :snapshotDate,
                    sle.student_id,
                    (MIN(sle.guardian_id::text))::uuid,
                    sle.subject_id,
                    sle.topic_id,
                    sle.skill_id,
                    MAX(sle.grade_level),
                    MAX(sle.target_school_system),
                    MAX(sle.country_code),
                    MAX(sle.locale),
                    COUNT(*) FILTER (WHERE sle.event_type = 'QUESTION_ANSWERED')::integer,
                    COUNT(*) FILTER (WHERE sle.event_type = 'QUESTION_ANSWERED' AND sle.is_correct = TRUE)::integer,
                    COUNT(*) FILTER (WHERE sle.event_type = 'QUESTION_ANSWERED' AND COALESCE(sle.is_correct, FALSE) = FALSE)::integer,
                    CASE
                        WHEN COUNT(*) FILTER (WHERE sle.event_type = 'QUESTION_ANSWERED') = 0 THEN 0
                        ELSE (
                            COUNT(*) FILTER (WHERE sle.event_type = 'QUESTION_ANSWERED' AND sle.is_correct = TRUE)::numeric
                            / COUNT(*) FILTER (WHERE sle.event_type = 'QUESTION_ANSWERED')::numeric
                        ) * 100
                    END,
                    COALESCE(SUM(sle.score) FILTER (WHERE sle.event_type = 'QUESTION_ANSWERED'), 0),
                    COALESCE(AVG(sle.score) FILTER (WHERE sle.event_type = 'QUESTION_ANSWERED'), 0),
                    COALESCE(SUM(COALESCE(sle.time_spent_seconds, 0)) FILTER (WHERE sle.event_type = 'QUESTION_ANSWERED'), 0)::integer,
                    COALESCE(AVG(sle.time_spent_seconds) FILTER (WHERE sle.event_type = 'QUESTION_ANSWERED'), 0),
                    COUNT(*) FILTER (WHERE sle.event_type = 'ACTIVITY_STARTED')::integer,
                    COUNT(*) FILTER (WHERE sle.event_type = 'ACTIVITY_COMPLETED')::integer,
                    COUNT(DISTINCT sle.assessment_attempt_id) FILTER (WHERE sle.event_type = 'ACTIVITY_COMPLETED')::integer,
                    MIN(sle.event_time),
                    MAX(sle.event_time),
                    NOW(),
                    NOW()
                FROM student_learning_events sle
                WHERE sle.event_date = :snapshotDate
                  AND sle.skill_id IS NOT NULL
                  AND sle.subject_id IS NOT NULL
                  AND sle.topic_id IS NOT NULL
                GROUP BY sle.student_id, sle.subject_id, sle.topic_id, sle.skill_id
                """)
                .param("snapshotDate", snapshotDate)
                .update();
    }

    public List<StudentSkillDailySnapshot> findByStudentAndDate(UUID studentId, LocalDate snapshotDate) {
        return jdbcClient.sql("""
                SELECT id, snapshot_date, student_id, guardian_id, subject_id, topic_id, skill_id,
                       grade_level, target_school_system, country_code, locale,
                       questions_answered, correct_answers, incorrect_answers,
                       accuracy, total_score, average_score,
                       total_time_spent_seconds, average_time_spent_seconds,
                       activities_started, activities_completed, attempts_completed,
                       first_event_at, last_event_at, created_at, updated_at
                FROM student_skill_daily_snapshots
                WHERE student_id = :studentId
                  AND snapshot_date = :snapshotDate
                ORDER BY subject_id, topic_id, skill_id
                """)
                .param("studentId", studentId)
                .param("snapshotDate", snapshotDate)
                .query((rs, rowNum) -> new StudentSkillDailySnapshot(
                        rs.getObject("id", UUID.class),
                        rs.getObject("snapshot_date", LocalDate.class),
                        rs.getObject("student_id", UUID.class),
                        rs.getObject("guardian_id", UUID.class),
                        rs.getObject("subject_id", UUID.class),
                        rs.getObject("topic_id", UUID.class),
                        rs.getObject("skill_id", UUID.class),
                        rs.getString("grade_level"),
                        rs.getString("target_school_system"),
                        rs.getString("country_code"),
                        rs.getString("locale"),
                        rs.getInt("questions_answered"),
                        rs.getInt("correct_answers"),
                        rs.getInt("incorrect_answers"),
                        rs.getBigDecimal("accuracy"),
                        rs.getBigDecimal("total_score"),
                        rs.getBigDecimal("average_score"),
                        rs.getInt("total_time_spent_seconds"),
                        rs.getBigDecimal("average_time_spent_seconds"),
                        rs.getInt("activities_started"),
                        rs.getInt("activities_completed"),
                        rs.getInt("attempts_completed"),
                        rs.getTimestamp("first_event_at") == null ? null : rs.getTimestamp("first_event_at").toInstant(),
                        rs.getTimestamp("last_event_at") == null ? null : rs.getTimestamp("last_event_at").toInstant(),
                        rs.getTimestamp("created_at").toInstant(),
                        rs.getTimestamp("updated_at").toInstant()
                ))
                .list();
    }
}
