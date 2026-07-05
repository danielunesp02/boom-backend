package com.boom.parentdashboard.realdata;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Component
public class ParentDashboardRealDataAdapter {

    private final JdbcClient jdbcClient;

    public ParentDashboardRealDataAdapter(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public ParentDashboardRealMetrics loadForStudent(UUID studentId, Integer periodDays) {
        int days = periodDays == null || periodDays <= 0 ? 30 : periodDays;
        LocalDate dateTo = LocalDate.now(ZoneOffset.UTC);
        LocalDate dateFrom = dateTo.minusDays(days - 1L);

        Summary summary = loadSummary(studentId, dateFrom, dateTo);

        if (summary.questionsAnswered() == 0 && summary.completedActivities() == 0) {
            return new ParentDashboardRealMetrics(
                    ParentDashboardRealDataStatus.NO_REAL_DATA,
                    studentId,
                    dateFrom.toString(),
                    dateTo.toString(),
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of()
            );
        }

        return new ParentDashboardRealMetrics(
                ParentDashboardRealDataStatus.REAL_DATA_AVAILABLE,
                studentId,
                dateFrom.toString(),
                dateTo.toString(),
                summary.questionsAnswered(),
                summary.correctAnswers(),
                summary.incorrectAnswers(),
                summary.accuracy(),
                summary.totalTimeSpentSeconds(),
                summary.completedActivities(),
                loadDailyActivityHistory(studentId, dateFrom, dateTo),
                loadRecentActivities(studentId, dateFrom, dateTo),
                loadLearningGaps(studentId),
                loadSubjectMetrics(studentId, dateFrom, dateTo),
                loadSkillMetrics(studentId, dateFrom, dateTo)
        );
    }

    private Summary loadSummary(UUID studentId, LocalDate dateFrom, LocalDate dateTo) {
        return jdbcClient.sql("""
                        SELECT
                            COALESCE(SUM(questions_answered), 0)::integer AS questions_answered,
                            COALESCE(SUM(correct_answers), 0)::integer AS correct_answers,
                            COALESCE(SUM(incorrect_answers), 0)::integer AS incorrect_answers,
                            CASE
                                WHEN COALESCE(SUM(questions_answered), 0) = 0 THEN 0
                                ELSE (
                                    COALESCE(SUM(correct_answers), 0)::numeric
                                    / COALESCE(SUM(questions_answered), 0)::numeric
                                ) * 100
                            END AS accuracy,
                            COALESCE(SUM(total_time_spent_seconds), 0)::integer AS total_time_spent_seconds,
                            COALESCE(SUM(activities_completed), 0)::integer AS completed_activities
                        FROM student_skill_daily_snapshots
                        WHERE student_id = :studentId
                          AND snapshot_date BETWEEN :dateFrom AND :dateTo
                        """)
                .param("studentId", studentId)
                .param("dateFrom", dateFrom)
                .param("dateTo", dateTo)
                .query((rs, rowNum) -> new Summary(
                        rs.getInt("questions_answered"),
                        rs.getInt("correct_answers"),
                        rs.getInt("incorrect_answers"),
                        rs.getDouble("accuracy"),
                        rs.getInt("total_time_spent_seconds"),
                        rs.getInt("completed_activities")
                ))
                .single();
    }


    private List<ParentDashboardRealMetrics.DailyActivityMetric> loadDailyActivityHistory(
            UUID studentId,
            LocalDate dateFrom,
            LocalDate dateTo
    ) {
        return jdbcClient.sql("""
                        SELECT
                            snapshot_date,
                            COALESCE(SUM(activities_completed), 0)::integer AS completed_activities,
                            COALESCE(SUM(questions_answered), 0)::integer AS questions_answered,
                            COALESCE(SUM(correct_answers), 0)::integer AS correct_answers,
                            CASE
                                WHEN COALESCE(SUM(questions_answered), 0) = 0 THEN NULL
                                ELSE ROUND(
                                    (
                                        COALESCE(SUM(correct_answers), 0)::numeric
                                        / COALESCE(SUM(questions_answered), 0)::numeric
                                    ) * 100
                                )::integer
                            END AS accuracy,
                            COALESCE(SUM(total_time_spent_seconds), 0)::integer AS total_time_spent_seconds
                        FROM student_skill_daily_snapshots
                        WHERE student_id = :studentId
                          AND snapshot_date BETWEEN :dateFrom AND :dateTo
                        GROUP BY snapshot_date
                        ORDER BY snapshot_date
                        """)
                .param("studentId", studentId)
                .param("dateFrom", dateFrom)
                .param("dateTo", dateTo)
                .query((rs, rowNum) -> new ParentDashboardRealMetrics.DailyActivityMetric(
                        rs.getObject("snapshot_date", LocalDate.class).toString(),
                        rs.getInt("completed_activities"),
                        rs.getObject("accuracy", Integer.class),
                        rs.getInt("total_time_spent_seconds")
                ))
                .list();
    }


    private List<ParentDashboardRealMetrics.RecentActivityMetric> loadRecentActivities(
            UUID studentId,
            LocalDate dateFrom,
            LocalDate dateTo
    ) {
        return jdbcClient.sql("""
                        SELECT
                            aa.activity_id,
                            aa.completed_at::date AS completed_date,
                            la.title AS activity_title,
                            ls.default_name AS subject_name,
                            ROUND(COALESCE(aa.accuracy, 0))::integer AS accuracy,
                            COALESCE(
                                SUM(ans.time_spent_seconds),
                                la.estimated_duration_minutes * 60,
                                0
                            )::integer AS duration_seconds,
                            COALESCE(aa.correct_answers, 0)::integer AS correct_answers,
                            COALESCE(aa.answered_questions, 0)::integer AS answered_questions
                        FROM assessment_attempts aa
                        JOIN learning_activities la ON la.id = aa.activity_id
                        JOIN learning_subjects ls ON ls.id = la.subject_id
                        LEFT JOIN answer_submissions ans ON ans.attempt_id = aa.id
                        WHERE aa.student_id = :studentId
                          AND aa.status = 'COMPLETED'
                          AND aa.completed_at IS NOT NULL
                          AND aa.completed_at::date BETWEEN :dateFrom AND :dateTo
                        GROUP BY
                            aa.id,
                            aa.activity_id,
                            aa.completed_at,
                            aa.accuracy,
                            aa.correct_answers,
                            aa.answered_questions,
                            la.title,
                            la.estimated_duration_minutes,
                            ls.default_name
                        ORDER BY aa.completed_at DESC
                        LIMIT 5
                        """)
                .param("studentId", studentId)
                .param("dateFrom", dateFrom)
                .param("dateTo", dateTo)
                .query((rs, rowNum) -> new ParentDashboardRealMetrics.RecentActivityMetric(
                        rs.getObject("activity_id", UUID.class).toString(),
                        rs.getObject("completed_date", LocalDate.class).toString(),
                        rs.getString("activity_title"),
                        rs.getString("subject_name"),
                        rs.getInt("accuracy"),
                        rs.getInt("duration_seconds"),
                        rs.getInt("correct_answers"),
                        rs.getInt("answered_questions")
                ))
                .list();
    }

private List<ParentDashboardRealMetrics.LearningGapMetric> loadLearningGaps(UUID studentId) {
    return jdbcClient.sql("""
                    SELECT
                        ssms.subject_id,
                        ls.default_name AS subject_name,
                        ssms.topic_id,
                        lt.default_name AS topic_name,
                        ssms.skill_id,
                        lsk.default_name AS skill_name,
                        ssms.mastery_status,
                        ROUND(COALESCE(ssms.accuracy, 0))::integer AS accuracy,
                        ROUND(COALESCE(ssms.mastery_score, 0))::integer AS mastery_score,
                        COALESCE(srq.priority, CASE
                            WHEN ssms.requires_reassessment THEN 'HIGH'
                            WHEN ssms.accuracy < 60 THEN 'HIGH'
                            WHEN ssms.accuracy < 75 THEN 'MEDIUM'
                            ELSE 'LOW'
                        END) AS priority,
                        COALESCE(srq.review_type, CASE
                            WHEN ssms.requires_reassessment THEN 'QUICK_DIAGNOSTIC'
                            WHEN ssms.accuracy < 60 THEN 'WORKED_EXAMPLE'
                            WHEN ssms.accuracy < 75 THEN 'GUIDED_PRACTICE'
                            ELSE 'RETRIEVAL_QUIZ'
                        END) AS review_type,
                        COALESCE(srq.status, 'SCHEDULED') AS review_status,
                        COALESCE(srq.reason, CASE
                            WHEN ssms.requires_reassessment THEN 'REASSESSMENT_REQUIRED'
                            WHEN ssms.accuracy < 75 THEN 'LOW_ACCURACY'
                            ELSE 'REVIEW_SCHEDULED'
                        END) AS reason,
                        COALESCE(srq.scheduled_for, ssms.next_review_at::date) AS next_review_date,
                        COALESCE(srq.requires_reassessment, ssms.requires_reassessment, FALSE) AS requires_reassessment,
                        COALESCE(srq.overdue_days, 0)::integer AS overdue_days
                    FROM student_skill_mastery_state ssms
                    JOIN learning_subjects ls ON ls.id = ssms.subject_id
                    JOIN learning_topics lt ON lt.id = ssms.topic_id
                    JOIN learning_skills lsk ON lsk.id = ssms.skill_id
                    LEFT JOIN LATERAL (
                        SELECT priority, review_type, status, reason, scheduled_for, requires_reassessment, overdue_days
                        FROM student_review_queue q
                        WHERE q.student_id = ssms.student_id
                          AND q.skill_id = ssms.skill_id
                          AND q.status IN ('SCHEDULED', 'DUE', 'OVERDUE', 'CRITICAL_OVERDUE')
                        ORDER BY
                            CASE q.status
                                WHEN 'CRITICAL_OVERDUE' THEN 1
                                WHEN 'OVERDUE' THEN 2
                                WHEN 'DUE' THEN 3
                                ELSE 4
                            END,
                            q.scheduled_for ASC,
                            q.created_at DESC
                        LIMIT 1
                    ) srq ON TRUE
                    WHERE ssms.student_id = :studentId
                      AND ssms.mastery_status IN ('ACTIVE_GAP', 'REASSESSMENT_REQUIRED', 'REGRESSED', 'LEARNING', 'REVIEW_SCHEDULED')
                    ORDER BY
                        CASE
                            WHEN COALESCE(srq.priority, '') = 'CRITICAL' THEN 1
                            WHEN COALESCE(srq.priority, '') = 'HIGH' THEN 2
                            WHEN COALESCE(srq.priority, '') = 'MEDIUM' THEN 3
                            ELSE 4
                        END,
                        COALESCE(srq.scheduled_for, ssms.next_review_at::date) ASC NULLS LAST,
                        ssms.accuracy ASC,
                        ssms.updated_at DESC
                    LIMIT 5
                    """)
            .param("studentId", studentId)
            .query((rs, rowNum) -> new ParentDashboardRealMetrics.LearningGapMetric(
                    rs.getObject("subject_id", UUID.class),
                    rs.getString("subject_name"),
                    rs.getObject("topic_id", UUID.class),
                    rs.getString("topic_name"),
                    rs.getObject("skill_id", UUID.class),
                    rs.getString("skill_name"),
                    rs.getString("mastery_status"),
                    rs.getInt("accuracy"),
                    rs.getInt("mastery_score"),
                    rs.getString("priority"),
                    rs.getString("review_type"),
                    rs.getString("review_status"),
                    rs.getString("reason"),
                    rs.getObject("next_review_date") == null ? null : rs.getObject("next_review_date", java.time.LocalDate.class).toString(),
                    rs.getBoolean("requires_reassessment"),
                    rs.getInt("overdue_days")
            ))
            .list();
}


    private List<ParentDashboardRealMetrics.SubjectMetric> loadSubjectMetrics(
            UUID studentId,
            LocalDate dateFrom,
            LocalDate dateTo
    ) {
        return jdbcClient.sql("""
                        SELECT
                            ssds.subject_id,
                            ls.default_name AS subject_name,
                            COALESCE(SUM(ssds.questions_answered), 0)::integer AS questions_answered,
                            COALESCE(SUM(ssds.correct_answers), 0)::integer AS correct_answers,
                            CASE
                                WHEN COALESCE(SUM(ssds.questions_answered), 0) = 0 THEN 0
                                ELSE (
                                    COALESCE(SUM(ssds.correct_answers), 0)::numeric
                                    / COALESCE(SUM(ssds.questions_answered), 0)::numeric
                                ) * 100
                            END AS accuracy,
                            COALESCE(SUM(ssds.total_time_spent_seconds), 0)::integer AS total_time_spent_seconds,
                            COALESCE(SUM(ssds.activities_completed), 0)::integer AS completed_activities
                        FROM student_skill_daily_snapshots ssds
                        JOIN learning_subjects ls ON ls.id = ssds.subject_id
                        WHERE ssds.student_id = :studentId
                          AND ssds.snapshot_date BETWEEN :dateFrom AND :dateTo
                        GROUP BY ssds.subject_id, ls.default_name
                        ORDER BY subject_name
                        """)
                .param("studentId", studentId)
                .param("dateFrom", dateFrom)
                .param("dateTo", dateTo)
                .query((rs, rowNum) -> new ParentDashboardRealMetrics.SubjectMetric(
                        rs.getObject("subject_id", UUID.class),
                        rs.getString("subject_name"),
                        rs.getInt("questions_answered"),
                        rs.getInt("correct_answers"),
                        rs.getDouble("accuracy"),
                        rs.getInt("total_time_spent_seconds"),
                        rs.getInt("completed_activities")
                ))
                .list();
    }

    private List<ParentDashboardRealMetrics.SkillMetric> loadSkillMetrics(
            UUID studentId,
            LocalDate dateFrom,
            LocalDate dateTo
    ) {
        return jdbcClient.sql("""
                        SELECT
                            ssds.subject_id,
                            ls.default_name AS subject_name,
                            ssds.topic_id,
                            lt.default_name AS topic_name,
                            ssds.skill_id,
                            lsk.default_name AS skill_name,
                            COALESCE(SUM(ssds.questions_answered), 0)::integer AS questions_answered,
                            COALESCE(SUM(ssds.correct_answers), 0)::integer AS correct_answers,
                            COALESCE(SUM(ssds.incorrect_answers), 0)::integer AS incorrect_answers,
                            CASE
                                WHEN COALESCE(SUM(ssds.questions_answered), 0) = 0 THEN 0
                                ELSE (
                                    COALESCE(SUM(ssds.correct_answers), 0)::numeric
                                    / COALESCE(SUM(ssds.questions_answered), 0)::numeric
                                ) * 100
                            END AS accuracy,
                            COALESCE(SUM(ssds.total_time_spent_seconds), 0)::integer AS total_time_spent_seconds,
                            COALESCE(SUM(ssds.activities_completed), 0)::integer AS completed_activities
                        FROM student_skill_daily_snapshots ssds
                        JOIN learning_subjects ls ON ls.id = ssds.subject_id
                        JOIN learning_topics lt ON lt.id = ssds.topic_id
                        JOIN learning_skills lsk ON lsk.id = ssds.skill_id
                        WHERE ssds.student_id = :studentId
                          AND ssds.snapshot_date BETWEEN :dateFrom AND :dateTo
                        GROUP BY ssds.subject_id, ls.default_name, ssds.topic_id, lt.default_name, ssds.skill_id, lsk.default_name
                        ORDER BY subject_name, topic_name, skill_name
                        """)
                .param("studentId", studentId)
                .param("dateFrom", dateFrom)
                .param("dateTo", dateTo)
                .query((rs, rowNum) -> new ParentDashboardRealMetrics.SkillMetric(
                        rs.getObject("subject_id", UUID.class),
                        rs.getString("subject_name"),
                        rs.getObject("topic_id", UUID.class),
                        rs.getString("topic_name"),
                        rs.getObject("skill_id", UUID.class),
                        rs.getString("skill_name"),
                        rs.getInt("questions_answered"),
                        rs.getInt("correct_answers"),
                        rs.getInt("incorrect_answers"),
                        rs.getDouble("accuracy"),
                        rs.getInt("total_time_spent_seconds"),
                        rs.getInt("completed_activities")
                ))
                .list();
    }

    private record Summary(
            int questionsAnswered,
            int correctAnswers,
            int incorrectAnswers,
            double accuracy,
            int totalTimeSpentSeconds,
            int completedActivities
    ) {
    }
}
