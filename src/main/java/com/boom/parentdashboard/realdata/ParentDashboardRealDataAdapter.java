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
