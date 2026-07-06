package com.boom.pedagogy.application;

import com.boom.pedagogy.api.dto.LearningCurvePointResponse;
import com.boom.pedagogy.api.dto.SubjectLearningStrategyResponse;
import com.boom.pedagogy.api.dto.UpdateSubjectLearningStrategyRequest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
public class SubjectLearningStrategyService {

    private static final int DEFAULT_WEEKS = 12;

    private final JdbcClient jdbcClient;

    public SubjectLearningStrategyService(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Transactional(readOnly = true)
    public List<SubjectLearningStrategyResponse> listStrategies(UUID studentId) {
        return jdbcClient.sql("""
                        SELECT
                            ls.id AS subject_id,
                            ls.default_name AS subject_name,
                            COALESCE(ssls.methodology_code, 'BALANCED') AS methodology_code,
                            COALESCE(ssls.calibration_status, 'NOT_CALIBRATED') AS calibration_status,
                            ssls.initial_assessment_score,
                            COALESCE(subject_mastery.current_mastery_score, ssls.current_mastery_score, 0) AS current_mastery_score,
                            COALESCE(ssls.expected_mastery_score, 80) AS expected_mastery_score,
                            COALESCE(ssls.high_performance_target_score, 90) AS high_performance_target_score,
                            COALESCE(ssls.weekly_study_minutes_goal, 120) AS weekly_study_minutes_goal,
                            COALESCE(ssls.curve_start_date, CURRENT_DATE) AS curve_start_date,
                            ssls.target_date
                        FROM learning_subjects ls
                        LEFT JOIN student_subject_learning_strategy ssls
                          ON ssls.subject_id = ls.id
                         AND ssls.student_id = :studentId
                        LEFT JOIN LATERAL (
                            SELECT ROUND(AVG(COALESCE(mastery_score, accuracy, 0)), 2) AS current_mastery_score
                            FROM student_skill_mastery_state state
                            WHERE state.student_id = :studentId
                              AND state.subject_id = ls.id
                        ) subject_mastery ON TRUE
                        ORDER BY ls.default_name
                        """)
                .param("studentId", studentId)
                .query((rs, rowNum) -> {
                    UUID subjectId = rs.getObject("subject_id", UUID.class);
                    LocalDate startDate = rs.getObject("curve_start_date", LocalDate.class);
                    LocalDate targetDate = rs.getObject("target_date", LocalDate.class);
                    if (targetDate == null) {
                        targetDate = startDate.plusWeeks(DEFAULT_WEEKS - 1L);
                    }

                    BigDecimal current = rs.getBigDecimal("current_mastery_score");
                    BigDecimal initial = rs.getBigDecimal("initial_assessment_score");
                    BigDecimal expectedTarget = rs.getBigDecimal("expected_mastery_score");
                    BigDecimal highTarget = rs.getBigDecimal("high_performance_target_score");
                    String methodology = rs.getString("methodology_code");

                    return new SubjectLearningStrategyResponse(
                            studentId,
                            subjectId,
                            rs.getString("subject_name"),
                            methodology,
                            rs.getString("calibration_status"),
                            initial,
                            current,
                            expectedTarget,
                            highTarget,
                            rs.getInt("weekly_study_minutes_goal"),
                            startDate,
                            targetDate,
                            buildCurvePoints(startDate, targetDate, initial, current, expectedTarget, highTarget, methodology)
                    );
                })
                .list();
    }

    @Transactional(readOnly = true)
    public SubjectLearningStrategyResponse getStrategy(UUID studentId, UUID subjectId) {
        return listStrategies(studentId).stream()
                .filter(strategy -> strategy.subjectId().equals(subjectId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Subject strategy not found."));
    }

    @Transactional
    public SubjectLearningStrategyResponse updateStrategy(UUID studentId, UpdateSubjectLearningStrategyRequest request) {
        if (request == null || request.subjectId() == null) {
            throw new IllegalArgumentException("subjectId is required.");
        }

        String methodologyCode = normalizeMethodology(request.methodologyCode());
        BigDecimal initialScore = clamp(request.initialAssessmentScore());
        int weeklyGoal = request.weeklyStudyMinutesGoal() == null || request.weeklyStudyMinutesGoal() <= 0
                ? 120
                : request.weeklyStudyMinutesGoal();

        LocalDate startDate = request.curveStartDate() == null ? LocalDate.now(ZoneOffset.UTC) : request.curveStartDate();
        LocalDate targetDate = request.targetDate() == null ? startDate.plusWeeks(DEFAULT_WEEKS - 1L) : request.targetDate();

        BigDecimal expectedTarget = expectedTarget(methodologyCode);
        BigDecimal highTarget = highPerformanceTarget(methodologyCode);

        jdbcClient.sql("""
                        INSERT INTO student_subject_learning_strategy (
                            student_id,
                            subject_id,
                            methodology_code,
                            initial_assessment_score,
                            current_mastery_score,
                            expected_mastery_score,
                            high_performance_target_score,
                            calibration_status,
                            weekly_study_minutes_goal,
                            curve_start_date,
                            target_date,
                            updated_at
                        )
                        VALUES (
                            :studentId,
                            :subjectId,
                            :methodologyCode,
                            :initialScore,
                            COALESCE(:initialScore, 0),
                            :expectedTarget,
                            :highTarget,
                            :calibrationStatus,
                            :weeklyGoal,
                            :startDate,
                            :targetDate,
                            CURRENT_TIMESTAMP
                        )
                        ON CONFLICT (student_id, subject_id)
                        DO UPDATE SET
                            methodology_code = EXCLUDED.methodology_code,
                            initial_assessment_score = EXCLUDED.initial_assessment_score,
                            current_mastery_score = COALESCE(EXCLUDED.initial_assessment_score, student_subject_learning_strategy.current_mastery_score),
                            expected_mastery_score = EXCLUDED.expected_mastery_score,
                            high_performance_target_score = EXCLUDED.high_performance_target_score,
                            calibration_status = EXCLUDED.calibration_status,
                            weekly_study_minutes_goal = EXCLUDED.weekly_study_minutes_goal,
                            curve_start_date = EXCLUDED.curve_start_date,
                            target_date = EXCLUDED.target_date,
                            updated_at = CURRENT_TIMESTAMP
                        """)
                .param("studentId", studentId)
                .param("subjectId", request.subjectId())
                .param("methodologyCode", methodologyCode)
                .param("initialScore", initialScore)
                .param("expectedTarget", expectedTarget)
                .param("highTarget", highTarget)
                .param("calibrationStatus", initialScore == null ? "NOT_CALIBRATED" : "CALIBRATED")
                .param("weeklyGoal", weeklyGoal)
                .param("startDate", startDate)
                .param("targetDate", targetDate)
                .update();

        return getStrategy(studentId, request.subjectId());
    }

    private List<LearningCurvePointResponse> buildCurvePoints(
            LocalDate startDate,
            LocalDate targetDate,
            BigDecimal initialAssessment,
            BigDecimal currentMastery,
            BigDecimal expectedTarget,
            BigDecimal highTarget,
            String methodologyCode
    ) {
        int weeks = Math.max(4, (int) ChronoUnit.WEEKS.between(startDate, targetDate) + 1);
        BigDecimal start = initialAssessment == null ? BigDecimal.ZERO : initialAssessment;
        int currentWeek = currentWeekIndex(startDate, weeks);

        return IntStream.range(0, weeks)
                .mapToObj(week -> {
                    LocalDate pointDate = startDate.plusWeeks(week);
                    BigDecimal actual = null;

                    if (currentMastery != null && week <= currentWeek) {
                        actual = curveScore(start, currentMastery, week, currentWeek + 1, methodologyCode, false);
                    }

                    return new LearningCurvePointResponse(
                            pointDate,
                            week,
                            actual,
                            curveScore(start, expectedTarget, week, weeks, methodologyCode, false),
                            curveScore(start, highTarget, week, weeks, methodologyCode, true)
                    );
                })
                .toList();
    }

    private int currentWeekIndex(LocalDate startDate, int weeks) {
        long diff = ChronoUnit.WEEKS.between(startDate, LocalDate.now(ZoneOffset.UTC));
        return (int) Math.max(0, Math.min(weeks - 1L, diff));
    }

    private BigDecimal curveScore(BigDecimal start, BigDecimal target, int weekIndex, int totalWeeks, String methodologyCode, boolean highPerformance) {
        if (totalWeeks <= 1) {
            return target.setScale(2, RoundingMode.HALF_UP);
        }

        double progress = (double) weekIndex / (double) (totalWeeks - 1);
        double exponent = curveExponent(methodologyCode, highPerformance);
        double shaped = 1.0 - Math.pow(1.0 - progress, exponent);
        double score = start.doubleValue() + (target.doubleValue() - start.doubleValue()) * shaped;

        return BigDecimal.valueOf(Math.max(0, Math.min(100, score))).setScale(2, RoundingMode.HALF_UP);
    }

    private double curveExponent(String methodologyCode, boolean highPerformance) {
        if (highPerformance) {
            return 1.75;
        }

        return switch (methodologyCode) {
            case "INTENSIVE_REMEDIATION" -> 1.45;
            case "EXAM_PREP" -> 1.60;
            case "LIGHT_REVIEW" -> 1.15;
            default -> 1.30;
        };
    }

    private String normalizeMethodology(String methodologyCode) {
        if (methodologyCode == null || methodologyCode.isBlank()) {
            return "BALANCED";
        }

        String normalized = methodologyCode.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "LIGHT_REVIEW", "BALANCED", "INTENSIVE_REMEDIATION", "EXAM_PREP" -> normalized;
            default -> throw new IllegalArgumentException("Unsupported methodologyCode: " + methodologyCode);
        };
    }

    private BigDecimal expectedTarget(String methodologyCode) {
        return switch (methodologyCode) {
            case "LIGHT_REVIEW" -> BigDecimal.valueOf(70);
            case "INTENSIVE_REMEDIATION" -> BigDecimal.valueOf(75);
            case "EXAM_PREP" -> BigDecimal.valueOf(85);
            default -> BigDecimal.valueOf(80);
        };
    }

    private BigDecimal highPerformanceTarget(String methodologyCode) {
        return "EXAM_PREP".equals(methodologyCode) ? BigDecimal.valueOf(95) : BigDecimal.valueOf(90);
    }

    private BigDecimal clamp(BigDecimal value) {
        if (value == null) {
            return null;
        }

        if (value.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }

        if (value.compareTo(BigDecimal.valueOf(100)) > 0) {
            return BigDecimal.valueOf(100);
        }

        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
