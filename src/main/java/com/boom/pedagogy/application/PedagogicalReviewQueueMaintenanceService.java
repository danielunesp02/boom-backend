package com.boom.pedagogy.application;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
public class PedagogicalReviewQueueMaintenanceService {

    private final JdbcClient jdbcClient;

    public PedagogicalReviewQueueMaintenanceService(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public PedagogicalReviewQueueMaintenanceResult refreshReviewQueue(String dateValue) {
        LocalDate today = parseDateOrToday(dateValue);

        int updatedQueueItems = jdbcClient.sql("""
                        UPDATE student_review_queue srq
                        SET overdue_days = GREATEST((:today::date - srq.scheduled_for), 0),
                            requires_reassessment =
                                GREATEST((:today::date - srq.scheduled_for), 0) >
                                COALESCE((lm.config_json ->> 'maxOverdueDaysBeforeReassessment')::integer, 7),
                            status = CASE
                                WHEN srq.scheduled_for > :today::date THEN 'SCHEDULED'
                                WHEN srq.scheduled_for = :today::date THEN 'DUE'
                                WHEN GREATEST((:today::date - srq.scheduled_for), 0) >
                                     COALESCE((lm.config_json ->> 'maxOverdueDaysBeforeReassessment')::integer, 7)
                                     THEN 'CRITICAL_OVERDUE'
                                ELSE 'OVERDUE'
                            END,
                            updated_at = NOW()
                        FROM learning_methodologies lm
                        WHERE lm.code = srq.methodology_code
                          AND srq.status IN ('SCHEDULED', 'DUE', 'OVERDUE', 'CRITICAL_OVERDUE')
                        """)
                .param("today", today)
                .update();

        int updatedMasteryStates = jdbcClient.sql("""
                        UPDATE student_skill_mastery_state ssms
                        SET requires_reassessment = TRUE,
                            mastery_status = CASE
                                WHEN mastery_status = 'MASTERED' THEN 'REGRESSED'
                                ELSE 'REASSESSMENT_REQUIRED'
                            END,
                            updated_at = NOW()
                        WHERE EXISTS (
                            SELECT 1
                            FROM student_review_queue srq
                            WHERE srq.student_id = ssms.student_id
                              AND srq.skill_id = ssms.skill_id
                              AND srq.status = 'CRITICAL_OVERDUE'
                              AND srq.requires_reassessment = TRUE
                        )
                        """)
                .update();

        int due = countByStatus("DUE");
        int overdue = countByStatus("OVERDUE");
        int criticalOverdue = countByStatus("CRITICAL_OVERDUE");

        return new PedagogicalReviewQueueMaintenanceResult(
                today.toString(),
                updatedQueueItems,
                updatedMasteryStates,
                due,
                overdue,
                criticalOverdue
        );
    }

    private int countByStatus(String status) {
        Integer count = jdbcClient.sql("""
                        SELECT COUNT(*)
                        FROM student_review_queue
                        WHERE status = :status
                        """)
                .param("status", status)
                .query(Integer.class)
                .single();

        return count == null ? 0 : count;
    }

    private LocalDate parseDateOrToday(String dateValue) {
        if (dateValue == null || dateValue.isBlank()) return LocalDate.now(ZoneOffset.UTC);
        return LocalDate.parse(dateValue);
    }
}
