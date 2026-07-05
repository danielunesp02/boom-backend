package com.boom.pedagogy.application;

import com.boom.student.attempt.domain.AssessmentAttempt;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class PedagogicalMasteryUpdateService {

    private static final String DEFAULT_METHODOLOGY_CODE = "BALANCED";

    private final JdbcClient jdbcClient;
    private final ObjectMapper objectMapper;

    public PedagogicalMasteryUpdateService(JdbcClient jdbcClient, ObjectMapper objectMapper) {
        this.jdbcClient = jdbcClient;
        this.objectMapper = objectMapper;
    }

    public PedagogicalMasteryResult updateAfterAttemptCompleted(AssessmentAttempt completedAttempt) {
        if (completedAttempt == null || completedAttempt.completedAt() == null) {
            throw new IllegalArgumentException("Only completed attempts can update mastery state.");
        }

        LearningActivityContext context = loadActivityContext(completedAttempt.activityId());
        StudentStrategy strategy = loadStudentStrategy(completedAttempt.studentId());
        StudentSkillMasterySnapshot previousState = loadCurrentMasteryState(completedAttempt.studentId(), context.skillId());

        int accuracy = normalizeAccuracy(completedAttempt.accuracy());
        int questionsAnswered = Math.max(completedAttempt.answeredQuestions(), 0);
        int correctAnswers = Math.max(completedAttempt.correctAnswers(), 0);
        int incorrectAnswers = Math.max(questionsAnswered - correctAnswers, 0);

        MasteryDecision decision = decideMastery(accuracy, questionsAnswered, strategy, previousState);
        Instant now = Instant.now();
        UUID masteryStateId = previousState == null ? UUID.randomUUID() : previousState.id();

        upsertMasteryState(masteryStateId, completedAttempt, context, strategy, previousState, decision,
                accuracy, questionsAnswered, correctAnswers, incorrectAnswers, completedAttempt.completedAt(), now);

        UUID reviewQueueId = null;
        if (decision.shouldCreateReviewQueue()) {
            supersedeOpenReviewItems(completedAttempt.studentId(), context.skillId(), now);
            reviewQueueId = createReviewQueueItem(completedAttempt, context, strategy, decision, now);
        }

        UUID interventionId = createLearningIntervention(completedAttempt, context, strategy, decision, reviewQueueId,
                accuracy, questionsAnswered, correctAnswers, incorrectAnswers, now);

        return new PedagogicalMasteryResult(completedAttempt.studentId(), completedAttempt.activityId(),
                context.subjectId(), context.topicId(), context.skillId(), strategy.methodologyCode(),
                decision.masteryStatus(), decision.reviewType(), decision.priority(), decision.reason(),
                decision.nextReviewDate(), reviewQueueId, interventionId);
    }

    private LearningActivityContext loadActivityContext(UUID activityId) {
        return jdbcClient.sql("""
                        SELECT subject_id, topic_id, skill_id
                        FROM learning_activities
                        WHERE id = :activityId
                        """)
                .param("activityId", activityId)
                .query((rs, rowNum) -> new LearningActivityContext(
                        rs.getObject("subject_id", UUID.class),
                        rs.getObject("topic_id", UUID.class),
                        rs.getObject("skill_id", UUID.class)))
                .single();
    }

    private StudentStrategy loadStudentStrategy(UUID studentId) {
        return jdbcClient.sql("""
                        SELECT lm.id AS methodology_id,
                               lm.code AS methodology_code,
                               COALESCE(sls.custom_config_json, lm.config_json) AS effective_config_json
                        FROM student_learning_strategy sls
                        JOIN learning_methodologies lm ON lm.id = sls.methodology_id
                        WHERE sls.student_id = :studentId
                          AND sls.active = TRUE
                          AND lm.active = TRUE
                        LIMIT 1
                        """)
                .param("studentId", studentId)
                .query((rs, rowNum) -> toStudentStrategy(
                        rs.getObject("methodology_id", UUID.class),
                        rs.getString("methodology_code"),
                        rs.getString("effective_config_json")))
                .optional()
                .orElseGet(() -> jdbcClient.sql("""
                                SELECT id AS methodology_id, code AS methodology_code, config_json AS effective_config_json
                                FROM learning_methodologies
                                WHERE code = :code AND active = TRUE
                                """)
                        .param("code", DEFAULT_METHODOLOGY_CODE)
                        .query((rs, rowNum) -> toStudentStrategy(
                                rs.getObject("methodology_id", UUID.class),
                                rs.getString("methodology_code"),
                                rs.getString("effective_config_json")))
                        .single());
    }

    private StudentStrategy toStudentStrategy(UUID methodologyId, String methodologyCode, String configJson) {
        JsonNode config = parseConfig(configJson);
        return new StudentStrategy(methodologyId, methodologyCode,
                getInt(config, "masteryThreshold", 80),
                getInt(config, "requiredSuccessfulReviews", 2),
                getInt(config, "maxOverdueDaysBeforeReassessment", 7),
                getIntArray(config, "reviewIntervalsDays", new int[]{1, 3, 7, 14, 30}));
    }

    private JsonNode parseConfig(String configJson) {
        try {
            return objectMapper.readTree(configJson == null || configJson.isBlank() ? "{}" : configJson);
        } catch (Exception ex) {
            throw new IllegalStateException("Invalid pedagogical methodology config_json.", ex);
        }
    }

    private int getInt(JsonNode config, String field, int fallback) {
        JsonNode value = config.get(field);
        return value == null || !value.isNumber() ? fallback : value.asInt();
    }

    private int[] getIntArray(JsonNode config, String field, int[] fallback) {
        JsonNode value = config.get(field);
        if (value == null || !value.isArray() || value.isEmpty()) return fallback;
        int[] result = new int[value.size()];
        for (int i = 0; i < value.size(); i++) result[i] = Math.max(value.get(i).asInt(), 0);
        return result;
    }

    private StudentSkillMasterySnapshot loadCurrentMasteryState(UUID studentId, UUID skillId) {
        return jdbcClient.sql("""
                        SELECT id, mastery_status, successful_reviews, failed_reviews, review_step_index,
                               consecutive_successes, consecutive_failures
                        FROM student_skill_mastery_state
                        WHERE student_id = :studentId AND skill_id = :skillId
                        """)
                .param("studentId", studentId)
                .param("skillId", skillId)
                .query((rs, rowNum) -> new StudentSkillMasterySnapshot(
                        rs.getObject("id", UUID.class), rs.getString("mastery_status"),
                        rs.getInt("successful_reviews"), rs.getInt("failed_reviews"),
                        rs.getInt("review_step_index"), rs.getInt("consecutive_successes"),
                        rs.getInt("consecutive_failures")))
                .optional()
                .orElse(null);
    }

    private MasteryDecision decideMastery(int accuracy, int questionsAnswered, StudentStrategy strategy, StudentSkillMasterySnapshot previousState) {
        int previousSuccessfulReviews = previousState == null ? 0 : previousState.successfulReviews();
        int previousFailedReviews = previousState == null ? 0 : previousState.failedReviews();
        int previousReviewStepIndex = previousState == null ? 0 : previousState.reviewStepIndex();

        if (questionsAnswered <= 0) {
            return failingDecision("REASSESSMENT_REQUIRED", "QUICK_DIAGNOSTIC", "HIGH", "FAILED_MASTERY_CHECK",
                    strategy, 0, previousFailedReviews + 1, 0, true);
        }

        if (accuracy >= strategy.masteryThreshold()) {
            int successfulReviews = previousSuccessfulReviews + 1;
            int nextReviewStepIndex = Math.min(previousReviewStepIndex + 1, strategy.reviewIntervalsDays().length - 1);
            int reviewIntervalDays = strategy.reviewIntervalsDays()[nextReviewStepIndex];
            if (successfulReviews >= strategy.requiredSuccessfulReviews()) {
                return new MasteryDecision("MASTERED", "MASTERY_CHECK", "LOW", "ACTIVE_GAP", successfulReviews,
                        previousFailedReviews, nextReviewStepIndex, reviewIntervalDays, null, false, false);
            }
            return new MasteryDecision("REVIEW_SCHEDULED", "MASTERY_CHECK", "LOW", "ACTIVE_GAP", successfulReviews,
                    previousFailedReviews, nextReviewStepIndex, reviewIntervalDays,
                    LocalDate.now(ZoneOffset.UTC).plusDays(reviewIntervalDays), true, false);
        }

        if (accuracy >= 75) {
            int reviewIntervalDays = strategy.reviewIntervalsDays()[0];
            return new MasteryDecision("LEARNING", "RETRIEVAL_QUIZ", "MEDIUM", "LOW_ACCURACY", previousSuccessfulReviews,
                    previousFailedReviews + 1, 0, reviewIntervalDays,
                    LocalDate.now(ZoneOffset.UTC).plusDays(reviewIntervalDays), true, false);
        }

        if (accuracy >= 60) {
            return failingDecision("ACTIVE_GAP", "GUIDED_PRACTICE", "MEDIUM", "LOW_ACCURACY", strategy,
                    previousSuccessfulReviews, previousFailedReviews + 1, 0, false);
        }

        return failingDecision("ACTIVE_GAP", "WORKED_EXAMPLE", "HIGH", "LOW_ACCURACY", strategy,
                previousSuccessfulReviews, previousFailedReviews + 1, 0, false);
    }

    private MasteryDecision failingDecision(String masteryStatus, String reviewType, String priority, String reason,
                                           StudentStrategy strategy, int successfulReviews, int failedReviews,
                                           int reviewStepIndex, boolean requiresReassessment) {
        int safeStepIndex = Math.min(Math.max(reviewStepIndex, 0), strategy.reviewIntervalsDays().length - 1);
        int reviewIntervalDays = strategy.reviewIntervalsDays()[safeStepIndex];
        return new MasteryDecision(masteryStatus, reviewType, priority, reason, successfulReviews, failedReviews,
                safeStepIndex, reviewIntervalDays, LocalDate.now(ZoneOffset.UTC).plusDays(reviewIntervalDays),
                true, requiresReassessment);
    }

    private void upsertMasteryState(UUID masteryStateId, AssessmentAttempt attempt, LearningActivityContext context,
                                    StudentStrategy strategy, StudentSkillMasterySnapshot previousState,
                                    MasteryDecision decision, int accuracy, int questionsAnswered, int correctAnswers,
                                    int incorrectAnswers, Instant lastPracticedAt, Instant now) {
        int previousConsecutiveSuccesses = previousState == null ? 0 : previousState.consecutiveSuccesses();
        int previousConsecutiveFailures = previousState == null ? 0 : previousState.consecutiveFailures();
        boolean success = accuracy >= strategy.masteryThreshold();
        int consecutiveSuccesses = success ? previousConsecutiveSuccesses + 1 : 0;
        int consecutiveFailures = success ? 0 : previousConsecutiveFailures + 1;

        jdbcClient.sql("""
                        INSERT INTO student_skill_mastery_state (
                            id, student_id, subject_id, topic_id, skill_id, mastery_status, mastery_score, accuracy,
                            questions_answered, correct_answers, incorrect_answers, consecutive_successes,
                            consecutive_failures, successful_reviews, failed_reviews, review_step_index,
                            review_interval_days, last_practiced_at, next_review_at, requires_reassessment,
                            confidence_score, created_at, updated_at
                        ) VALUES (
                            :id, :studentId, :subjectId, :topicId, :skillId, :masteryStatus, :masteryScore, :accuracy,
                            :questionsAnswered, :correctAnswers, :incorrectAnswers, :consecutiveSuccesses,
                            :consecutiveFailures, :successfulReviews, :failedReviews, :reviewStepIndex,
                            :reviewIntervalDays, :lastPracticedAt, :nextReviewAt, :requiresReassessment,
                            :confidenceScore, :now, :now
                        )
                        ON CONFLICT (student_id, skill_id) DO UPDATE SET
                            subject_id = EXCLUDED.subject_id,
                            topic_id = EXCLUDED.topic_id,
                            mastery_status = EXCLUDED.mastery_status,
                            mastery_score = EXCLUDED.mastery_score,
                            accuracy = EXCLUDED.accuracy,
                            questions_answered = EXCLUDED.questions_answered,
                            correct_answers = EXCLUDED.correct_answers,
                            incorrect_answers = EXCLUDED.incorrect_answers,
                            consecutive_successes = EXCLUDED.consecutive_successes,
                            consecutive_failures = EXCLUDED.consecutive_failures,
                            successful_reviews = EXCLUDED.successful_reviews,
                            failed_reviews = EXCLUDED.failed_reviews,
                            review_step_index = EXCLUDED.review_step_index,
                            review_interval_days = EXCLUDED.review_interval_days,
                            last_practiced_at = EXCLUDED.last_practiced_at,
                            next_review_at = EXCLUDED.next_review_at,
                            requires_reassessment = EXCLUDED.requires_reassessment,
                            confidence_score = EXCLUDED.confidence_score,
                            updated_at = EXCLUDED.updated_at
                        """)
                .param("id", masteryStateId)
                .param("studentId", attempt.studentId())
                .param("subjectId", context.subjectId())
                .param("topicId", context.topicId())
                .param("skillId", context.skillId())
                .param("masteryStatus", decision.masteryStatus())
                .param("masteryScore", BigDecimal.valueOf(accuracy))
                .param("accuracy", BigDecimal.valueOf(accuracy))
                .param("questionsAnswered", questionsAnswered)
                .param("correctAnswers", correctAnswers)
                .param("incorrectAnswers", incorrectAnswers)
                .param("consecutiveSuccesses", consecutiveSuccesses)
                .param("consecutiveFailures", consecutiveFailures)
                .param("successfulReviews", decision.successfulReviews())
                .param("failedReviews", decision.failedReviews())
                .param("reviewStepIndex", decision.reviewStepIndex())
                .param("reviewIntervalDays", decision.reviewIntervalDays())
                .param("lastPracticedAt", lastPracticedAt)
                .param("nextReviewAt", decision.nextReviewDate() == null ? null : decision.nextReviewDate().atStartOfDay().toInstant(ZoneOffset.UTC))
                .param("requiresReassessment", decision.requiresReassessment())
                .param("confidenceScore", calculateConfidenceScore(accuracy, questionsAnswered, decision))
                .param("now", now)
                .update();
    }

    private void supersedeOpenReviewItems(UUID studentId, UUID skillId, Instant now) {
        jdbcClient.sql("""
                        UPDATE student_review_queue
                        SET status = 'SUPERSEDED', updated_at = :now
                        WHERE student_id = :studentId AND skill_id = :skillId
                          AND status IN ('SCHEDULED', 'DUE', 'OVERDUE', 'CRITICAL_OVERDUE')
                        """)
                .param("studentId", studentId)
                .param("skillId", skillId)
                .param("now", now)
                .update();
    }

    private UUID createReviewQueueItem(AssessmentAttempt attempt, LearningActivityContext context, StudentStrategy strategy,
                                       MasteryDecision decision, Instant now) {
        UUID id = UUID.randomUUID();
        jdbcClient.sql("""
                        INSERT INTO student_review_queue (
                            id, student_id, subject_id, topic_id, skill_id, source_attempt_id, source_event_id,
                            reason, priority, scheduled_for, status, review_type, methodology_code, overdue_days,
                            requires_reassessment, created_at, completed_at, updated_at
                        ) VALUES (
                            :id, :studentId, :subjectId, :topicId, :skillId, :sourceAttemptId, NULL,
                            :reason, :priority, :scheduledFor, 'SCHEDULED', :reviewType, :methodologyCode, 0,
                            :requiresReassessment, :now, NULL, :now
                        )
                        """)
                .param("id", id)
                .param("studentId", attempt.studentId())
                .param("subjectId", context.subjectId())
                .param("topicId", context.topicId())
                .param("skillId", context.skillId())
                .param("sourceAttemptId", attempt.id())
                .param("reason", decision.reason())
                .param("priority", decision.priority())
                .param("scheduledFor", decision.nextReviewDate())
                .param("reviewType", decision.reviewType())
                .param("methodologyCode", strategy.methodologyCode())
                .param("requiresReassessment", decision.requiresReassessment())
                .param("now", now)
                .update();
        return id;
    }

    private UUID createLearningIntervention(AssessmentAttempt attempt, LearningActivityContext context, StudentStrategy strategy,
                                            MasteryDecision decision, UUID reviewQueueId, int accuracy, int questionsAnswered,
                                            int correctAnswers, int incorrectAnswers, Instant now) {
        UUID id = UUID.randomUUID();
        String inputSnapshotJson = """
                {"source":"assessment_attempt_completed","attemptId":"%s","studentId":"%s","activityId":"%s","accuracy":%d,"questionsAnswered":%d,"correctAnswers":%d,"incorrectAnswers":%d,"methodologyCode":"%s"}
                """.formatted(attempt.id(), attempt.studentId(), attempt.activityId(), accuracy, questionsAnswered,
                correctAnswers, incorrectAnswers, strategy.methodologyCode());
        String recommendationJson = """
                {"masteryStatus":"%s","reviewType":"%s","priority":"%s","reason":"%s","nextReviewDate":%s,"aiRequired":false}
                """.formatted(decision.masteryStatus(), decision.reviewType(), decision.priority(), decision.reason(),
                decision.nextReviewDate() == null ? "null" : "\"" + decision.nextReviewDate() + "\"");

        jdbcClient.sql("""
                        INSERT INTO student_learning_interventions (
                            id, student_id, subject_id, topic_id, skill_id, review_queue_id, methodology_id,
                            intervention_type, trigger_reason, input_snapshot_json, recommendation_json, ai_used,
                            ai_provider, ai_model, status, created_at, completed_at, updated_at
                        ) VALUES (
                            :id, :studentId, :subjectId, :topicId, :skillId, :reviewQueueId, :methodologyId,
                            :interventionType, :triggerReason, CAST(:inputSnapshotJson AS jsonb),
                            CAST(:recommendationJson AS jsonb), FALSE, NULL, NULL, 'CREATED', :now, NULL, :now
                        )
                        """)
                .param("id", id)
                .param("studentId", attempt.studentId())
                .param("subjectId", context.subjectId())
                .param("topicId", context.topicId())
                .param("skillId", context.skillId())
                .param("reviewQueueId", reviewQueueId)
                .param("methodologyId", strategy.methodologyId())
                .param("interventionType", "REVIEW_RECOMMENDATION")
                .param("triggerReason", decision.reason())
                .param("inputSnapshotJson", inputSnapshotJson)
                .param("recommendationJson", recommendationJson)
                .param("now", now)
                .update();
        return id;
    }

    private BigDecimal calculateConfidenceScore(int accuracy, int questionsAnswered, MasteryDecision decision) {
        double sampleFactor = Math.min(1.0, questionsAnswered / 10.0);
        double statusFactor = "MASTERED".equals(decision.masteryStatus()) ? 1.0 : 0.75;
        double score = Math.max(0, Math.min(100, accuracy * sampleFactor * statusFactor));
        return BigDecimal.valueOf(score);
    }

    private int normalizeAccuracy(BigDecimal accuracy) {
        if (accuracy == null) return 0;
        if (accuracy.compareTo(BigDecimal.ONE) <= 0) {
            return accuracy.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).intValue();
        }
        int value = accuracy.setScale(0, RoundingMode.HALF_UP).intValue();
        return Math.max(0, Math.min(100, value));
    }

    private record LearningActivityContext(UUID subjectId, UUID topicId, UUID skillId) {}
    private record StudentStrategy(UUID methodologyId, String methodologyCode, int masteryThreshold,
                                   int requiredSuccessfulReviews, int maxOverdueDaysBeforeReassessment,
                                   int[] reviewIntervalsDays) {}
    private record StudentSkillMasterySnapshot(UUID id, String masteryStatus, int successfulReviews, int failedReviews,
                                               int reviewStepIndex, int consecutiveSuccesses, int consecutiveFailures) {}
    private record MasteryDecision(String masteryStatus, String reviewType, String priority, String reason,
                                   int successfulReviews, int failedReviews, int reviewStepIndex, int reviewIntervalDays,
                                   LocalDate nextReviewDate, boolean shouldCreateReviewQueue,
                                   boolean requiresReassessment) {}
}
