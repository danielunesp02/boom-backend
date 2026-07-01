package com.boom.learning.repository;

import com.boom.learning.domain.*;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class LearningActivityRepository {

    private final JdbcClient jdbcClient;

    public LearningActivityRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<LearningActivity> findActiveActivities(UUID subjectId, UUID topicId, UUID skillId, UUID objectiveId) {
        StringBuilder sql = new StringBuilder("""
                SELECT id, code, title, description, subject_id, topic_id, skill_id, objective_id,
                       curriculum_framework_id, curriculum_band_id, curriculum_expectation_id,
                       activity_type, estimated_duration_minutes, complexity_level, depth_level,
                       display_order, status, created_at, updated_at
                FROM learning_activities
                WHERE status = 'ACTIVE'
                """);

        if (subjectId != null) {
            sql.append(" AND subject_id = :subjectId");
        }
        if (topicId != null) {
            sql.append(" AND topic_id = :topicId");
        }
        if (skillId != null) {
            sql.append(" AND skill_id = :skillId");
        }
        if (objectiveId != null) {
            sql.append(" AND objective_id = :objectiveId");
        }

        sql.append(" ORDER BY display_order, title");

        var spec = jdbcClient.sql(sql.toString());

        if (subjectId != null) {
            spec = spec.param("subjectId", subjectId);
        }
        if (topicId != null) {
            spec = spec.param("topicId", topicId);
        }
        if (skillId != null) {
            spec = spec.param("skillId", skillId);
        }
        if (objectiveId != null) {
            spec = spec.param("objectiveId", objectiveId);
        }

        return spec.query((rs, rowNum) -> mapActivity(rs)).list();
    }

    public Optional<LearningActivity> findActiveActivityById(UUID activityId) {
        return jdbcClient.sql("""
                        SELECT id, code, title, description, subject_id, topic_id, skill_id, objective_id,
                               curriculum_framework_id, curriculum_band_id, curriculum_expectation_id,
                               activity_type, estimated_duration_minutes, complexity_level, depth_level,
                               display_order, status, created_at, updated_at
                        FROM learning_activities
                        WHERE id = :activityId
                          AND status = 'ACTIVE'
                        """)
                .param("activityId", activityId)
                .query((rs, rowNum) -> mapActivity(rs))
                .optional();
    }

    public List<ActivityQuestion> findActiveQuestionsByActivity(UUID activityId) {
        return jdbcClient.sql("""
                        SELECT id, activity_id, code, prompt, explanation, question_type,
                               subject_id, topic_id, skill_id, objective_id,
                               complexity_level, depth_level, display_order, status, created_at, updated_at
                        FROM activity_questions
                        WHERE activity_id = :activityId
                          AND status = 'ACTIVE'
                        ORDER BY display_order, code
                        """)
                .param("activityId", activityId)
                .query((rs, rowNum) -> new ActivityQuestion(
                        rs.getObject("id", UUID.class),
                        rs.getObject("activity_id", UUID.class),
                        rs.getString("code"),
                        rs.getString("prompt"),
                        rs.getString("explanation"),
                        QuestionType.valueOf(rs.getString("question_type")),
                        rs.getObject("subject_id", UUID.class),
                        rs.getObject("topic_id", UUID.class),
                        rs.getObject("skill_id", UUID.class),
                        rs.getObject("objective_id", UUID.class),
                        ComplexityLevel.valueOf(rs.getString("complexity_level")),
                        DepthLevel.valueOf(rs.getString("depth_level")),
                        rs.getInt("display_order"),
                        LearningStatus.valueOf(rs.getString("status")),
                        rs.getTimestamp("created_at").toInstant(),
                        rs.getTimestamp("updated_at").toInstant()
                ))
                .list();
    }

    public List<QuestionOption> findActiveOptionsByQuestion(UUID questionId) {
        return jdbcClient.sql("""
                        SELECT id, question_id, label, option_text, is_correct, display_order, status, created_at, updated_at
                        FROM question_options
                        WHERE question_id = :questionId
                          AND status = 'ACTIVE'
                        ORDER BY display_order, label
                        """)
                .param("questionId", questionId)
                .query((rs, rowNum) -> new QuestionOption(
                        rs.getObject("id", UUID.class),
                        rs.getObject("question_id", UUID.class),
                        rs.getString("label"),
                        rs.getString("option_text"),
                        rs.getBoolean("is_correct"),
                        rs.getInt("display_order"),
                        LearningStatus.valueOf(rs.getString("status")),
                        rs.getTimestamp("created_at").toInstant(),
                        rs.getTimestamp("updated_at").toInstant()
                ))
                .list();
    }

    public UUID upsertActivity(
            String code,
            String title,
            String description,
            UUID subjectId,
            UUID topicId,
            UUID skillId,
            UUID objectiveId,
            UUID curriculumFrameworkId,
            UUID curriculumBandId,
            UUID curriculumExpectationId,
            LearningActivityType activityType,
            int estimatedDurationMinutes,
            ComplexityLevel complexityLevel,
            DepthLevel depthLevel,
            int displayOrder
    ) {
        UUID existingId = jdbcClient.sql("SELECT id FROM learning_activities WHERE code = :code")
                .param("code", code)
                .query(UUID.class)
                .optional()
                .orElse(null);

        if (existingId != null) {
            jdbcClient.sql("""
                            UPDATE learning_activities
                            SET title = :title,
                                description = :description,
                                subject_id = :subjectId,
                                topic_id = :topicId,
                                skill_id = :skillId,
                                objective_id = :objectiveId,
                                curriculum_framework_id = :curriculumFrameworkId,
                                curriculum_band_id = :curriculumBandId,
                                curriculum_expectation_id = :curriculumExpectationId,
                                activity_type = :activityType,
                                estimated_duration_minutes = :estimatedDurationMinutes,
                                complexity_level = :complexityLevel,
                                depth_level = :depthLevel,
                                display_order = :displayOrder,
                                status = 'ACTIVE',
                                updated_at = :updatedAt
                            WHERE id = :id
                            """)
                    .param("id", existingId)
                    .param("title", title)
                    .param("description", description)
                    .param("subjectId", subjectId)
                    .param("topicId", topicId)
                    .param("skillId", skillId)
                    .param("objectiveId", objectiveId)
                    .param("curriculumFrameworkId", curriculumFrameworkId)
                    .param("curriculumBandId", curriculumBandId)
                    .param("curriculumExpectationId", curriculumExpectationId)
                    .param("activityType", activityType.name())
                    .param("estimatedDurationMinutes", estimatedDurationMinutes)
                    .param("complexityLevel", complexityLevel.name())
                    .param("depthLevel", depthLevel.name())
                    .param("displayOrder", displayOrder)
                    .param("updatedAt", Timestamp.from(Instant.now()))
                    .update();
            return existingId;
        }

        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        jdbcClient.sql("""
                        INSERT INTO learning_activities (
                            id, code, title, description, subject_id, topic_id, skill_id, objective_id,
                            curriculum_framework_id, curriculum_band_id, curriculum_expectation_id,
                            activity_type, estimated_duration_minutes, complexity_level, depth_level,
                            display_order, status, created_at, updated_at
                        )
                        VALUES (
                            :id, :code, :title, :description, :subjectId, :topicId, :skillId, :objectiveId,
                            :curriculumFrameworkId, :curriculumBandId, :curriculumExpectationId,
                            :activityType, :estimatedDurationMinutes, :complexityLevel, :depthLevel,
                            :displayOrder, 'ACTIVE', :createdAt, :updatedAt
                        )
                        """)
                .param("id", id)
                .param("code", code)
                .param("title", title)
                .param("description", description)
                .param("subjectId", subjectId)
                .param("topicId", topicId)
                .param("skillId", skillId)
                .param("objectiveId", objectiveId)
                .param("curriculumFrameworkId", curriculumFrameworkId)
                .param("curriculumBandId", curriculumBandId)
                .param("curriculumExpectationId", curriculumExpectationId)
                .param("activityType", activityType.name())
                .param("estimatedDurationMinutes", estimatedDurationMinutes)
                .param("complexityLevel", complexityLevel.name())
                .param("depthLevel", depthLevel.name())
                .param("displayOrder", displayOrder)
                .param("createdAt", Timestamp.from(now))
                .param("updatedAt", Timestamp.from(now))
                .update();

        return id;
    }

    public UUID upsertQuestion(
            UUID activityId,
            String code,
            String prompt,
            String explanation,
            QuestionType questionType,
            UUID subjectId,
            UUID topicId,
            UUID skillId,
            UUID objectiveId,
            ComplexityLevel complexityLevel,
            DepthLevel depthLevel,
            int displayOrder
    ) {
        UUID existingId = jdbcClient.sql("SELECT id FROM activity_questions WHERE activity_id = :activityId AND code = :code")
                .param("activityId", activityId)
                .param("code", code)
                .query(UUID.class)
                .optional()
                .orElse(null);

        if (existingId != null) {
            jdbcClient.sql("""
                            UPDATE activity_questions
                            SET prompt = :prompt,
                                explanation = :explanation,
                                question_type = :questionType,
                                subject_id = :subjectId,
                                topic_id = :topicId,
                                skill_id = :skillId,
                                objective_id = :objectiveId,
                                complexity_level = :complexityLevel,
                                depth_level = :depthLevel,
                                display_order = :displayOrder,
                                status = 'ACTIVE',
                                updated_at = :updatedAt
                            WHERE id = :id
                            """)
                    .param("id", existingId)
                    .param("prompt", prompt)
                    .param("explanation", explanation)
                    .param("questionType", questionType.name())
                    .param("subjectId", subjectId)
                    .param("topicId", topicId)
                    .param("skillId", skillId)
                    .param("objectiveId", objectiveId)
                    .param("complexityLevel", complexityLevel.name())
                    .param("depthLevel", depthLevel.name())
                    .param("displayOrder", displayOrder)
                    .param("updatedAt", Timestamp.from(Instant.now()))
                    .update();
            return existingId;
        }

        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        jdbcClient.sql("""
                        INSERT INTO activity_questions (
                            id, activity_id, code, prompt, explanation, question_type,
                            subject_id, topic_id, skill_id, objective_id,
                            complexity_level, depth_level, display_order, status, created_at, updated_at
                        )
                        VALUES (
                            :id, :activityId, :code, :prompt, :explanation, :questionType,
                            :subjectId, :topicId, :skillId, :objectiveId,
                            :complexityLevel, :depthLevel, :displayOrder, 'ACTIVE', :createdAt, :updatedAt
                        )
                        """)
                .param("id", id)
                .param("activityId", activityId)
                .param("code", code)
                .param("prompt", prompt)
                .param("explanation", explanation)
                .param("questionType", questionType.name())
                .param("subjectId", subjectId)
                .param("topicId", topicId)
                .param("skillId", skillId)
                .param("objectiveId", objectiveId)
                .param("complexityLevel", complexityLevel.name())
                .param("depthLevel", depthLevel.name())
                .param("displayOrder", displayOrder)
                .param("createdAt", Timestamp.from(now))
                .param("updatedAt", Timestamp.from(now))
                .update();

        return id;
    }

    public UUID upsertOption(UUID questionId, String label, String text, boolean correct, int displayOrder) {
        UUID existingId = jdbcClient.sql("SELECT id FROM question_options WHERE question_id = :questionId AND label = :label")
                .param("questionId", questionId)
                .param("label", label)
                .query(UUID.class)
                .optional()
                .orElse(null);

        if (existingId != null) {
            jdbcClient.sql("""
                            UPDATE question_options
                            SET option_text = :text,
                                is_correct = :correct,
                                display_order = :displayOrder,
                                status = 'ACTIVE',
                                updated_at = :updatedAt
                            WHERE id = :id
                            """)
                    .param("id", existingId)
                    .param("text", text)
                    .param("correct", correct)
                    .param("displayOrder", displayOrder)
                    .param("updatedAt", Timestamp.from(Instant.now()))
                    .update();
            return existingId;
        }

        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        jdbcClient.sql("""
                        INSERT INTO question_options (
                            id, question_id, label, option_text, is_correct, display_order, status, created_at, updated_at
                        )
                        VALUES (
                            :id, :questionId, :label, :text, :correct, :displayOrder, 'ACTIVE', :createdAt, :updatedAt
                        )
                        """)
                .param("id", id)
                .param("questionId", questionId)
                .param("label", label)
                .param("text", text)
                .param("correct", correct)
                .param("displayOrder", displayOrder)
                .param("createdAt", Timestamp.from(now))
                .param("updatedAt", Timestamp.from(now))
                .update();

        return id;
    }

    private LearningActivity mapActivity(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new LearningActivity(
                rs.getObject("id", UUID.class),
                rs.getString("code"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getObject("subject_id", UUID.class),
                rs.getObject("topic_id", UUID.class),
                rs.getObject("skill_id", UUID.class),
                rs.getObject("objective_id", UUID.class),
                rs.getObject("curriculum_framework_id", UUID.class),
                rs.getObject("curriculum_band_id", UUID.class),
                rs.getObject("curriculum_expectation_id", UUID.class),
                LearningActivityType.valueOf(rs.getString("activity_type")),
                rs.getInt("estimated_duration_minutes"),
                ComplexityLevel.valueOf(rs.getString("complexity_level")),
                DepthLevel.valueOf(rs.getString("depth_level")),
                rs.getInt("display_order"),
                LearningStatus.valueOf(rs.getString("status")),
                rs.getTimestamp("created_at").toInstant(),
                rs.getTimestamp("updated_at").toInstant()
        );
    }
}
