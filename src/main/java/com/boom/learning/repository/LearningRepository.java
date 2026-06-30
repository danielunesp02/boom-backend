package com.boom.learning.repository;

import com.boom.learning.domain.*;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public class LearningRepository {
    private final JdbcClient jdbcClient;
    public LearningRepository(JdbcClient jdbcClient) { this.jdbcClient = jdbcClient; }

    public List<LearningSubject> findActiveSubjects() {
        return jdbcClient.sql("SELECT id, code, default_name, description, status, created_at, updated_at FROM learning_subjects WHERE status='ACTIVE' ORDER BY default_name")
                .query((rs, n) -> new LearningSubject(rs.getObject("id", UUID.class), rs.getString("code"), rs.getString("default_name"), rs.getString("description"), LearningStatus.valueOf(rs.getString("status")), rs.getTimestamp("created_at").toInstant(), rs.getTimestamp("updated_at").toInstant())).list();
    }
    public List<LearningTopic> findActiveTopicsBySubject(UUID subjectId) {
        return jdbcClient.sql("SELECT id, subject_id, code, default_name, description, display_order, status, created_at, updated_at FROM learning_topics WHERE subject_id=:subjectId AND status='ACTIVE' ORDER BY display_order, default_name")
                .param("subjectId", subjectId).query((rs, n) -> new LearningTopic(rs.getObject("id", UUID.class), rs.getObject("subject_id", UUID.class), rs.getString("code"), rs.getString("default_name"), rs.getString("description"), rs.getInt("display_order"), LearningStatus.valueOf(rs.getString("status")), rs.getTimestamp("created_at").toInstant(), rs.getTimestamp("updated_at").toInstant())).list();
    }
    public List<LearningSkill> findActiveSkillsByTopic(UUID topicId) {
        return jdbcClient.sql("SELECT id, topic_id, code, default_name, description, display_order, status, created_at, updated_at FROM learning_skills WHERE topic_id=:topicId AND status='ACTIVE' ORDER BY display_order, default_name")
                .param("topicId", topicId).query((rs, n) -> new LearningSkill(rs.getObject("id", UUID.class), rs.getObject("topic_id", UUID.class), rs.getString("code"), rs.getString("default_name"), rs.getString("description"), rs.getInt("display_order"), LearningStatus.valueOf(rs.getString("status")), rs.getTimestamp("created_at").toInstant(), rs.getTimestamp("updated_at").toInstant())).list();
    }
    public List<LearningObjective> findActiveObjectivesBySkill(UUID skillId) {
        return jdbcClient.sql("SELECT id, skill_id, code, description, complexity_level, depth_level, display_order, status, created_at, updated_at FROM learning_objectives WHERE skill_id=:skillId AND status='ACTIVE' ORDER BY display_order, code")
                .param("skillId", skillId).query((rs, n) -> new LearningObjective(rs.getObject("id", UUID.class), rs.getObject("skill_id", UUID.class), rs.getString("code"), rs.getString("description"), ComplexityLevel.valueOf(rs.getString("complexity_level")), DepthLevel.valueOf(rs.getString("depth_level")), rs.getInt("display_order"), LearningStatus.valueOf(rs.getString("status")), rs.getTimestamp("created_at").toInstant(), rs.getTimestamp("updated_at").toInstant())).list();
    }
    public UUID upsertSubject(String code, String name, String description) {
        UUID id = jdbcClient.sql("SELECT id FROM learning_subjects WHERE code=:code").param("code", code).query(UUID.class).optional().orElse(UUID.randomUUID());
        Instant now=Instant.now();
        jdbcClient.sql("INSERT INTO learning_subjects(id,code,default_name,description,status,created_at,updated_at) VALUES(:id,:code,:name,:description,'ACTIVE',:now,:now) ON CONFLICT(code) DO UPDATE SET default_name=EXCLUDED.default_name, description=EXCLUDED.description, status='ACTIVE', updated_at=EXCLUDED.updated_at")
                .param("id",id).param("code",code).param("name",name).param("description",description).param("now", Timestamp.from(now)).update();
        return id;
    }
    public UUID upsertTopic(UUID subjectId, String code, String name, String description, int order) {
        UUID id = jdbcClient.sql("SELECT id FROM learning_topics WHERE subject_id=:subjectId AND code=:code").param("subjectId",subjectId).param("code",code).query(UUID.class).optional().orElse(UUID.randomUUID());
        Instant now=Instant.now();
        jdbcClient.sql("INSERT INTO learning_topics(id,subject_id,code,default_name,description,display_order,status,created_at,updated_at) VALUES(:id,:subjectId,:code,:name,:description,:order,'ACTIVE',:now,:now) ON CONFLICT(subject_id,code) DO UPDATE SET default_name=EXCLUDED.default_name, description=EXCLUDED.description, display_order=EXCLUDED.display_order, status='ACTIVE', updated_at=EXCLUDED.updated_at")
                .param("id",id).param("subjectId",subjectId).param("code",code).param("name",name).param("description",description).param("order",order).param("now",Timestamp.from(now)).update();
        return id;
    }
    public UUID upsertSkill(UUID topicId, String code, String name, String description, int order) {
        UUID id = jdbcClient.sql("SELECT id FROM learning_skills WHERE topic_id=:topicId AND code=:code").param("topicId",topicId).param("code",code).query(UUID.class).optional().orElse(UUID.randomUUID());
        Instant now=Instant.now();
        jdbcClient.sql("INSERT INTO learning_skills(id,topic_id,code,default_name,description,display_order,status,created_at,updated_at) VALUES(:id,:topicId,:code,:name,:description,:order,'ACTIVE',:now,:now) ON CONFLICT(topic_id,code) DO UPDATE SET default_name=EXCLUDED.default_name, description=EXCLUDED.description, display_order=EXCLUDED.display_order, status='ACTIVE', updated_at=EXCLUDED.updated_at")
                .param("id",id).param("topicId",topicId).param("code",code).param("name",name).param("description",description).param("order",order).param("now",Timestamp.from(now)).update();
        return id;
    }
    public UUID upsertObjective(UUID skillId, String code, String description, ComplexityLevel complexity, DepthLevel depth, int order) {
        UUID id = jdbcClient.sql("SELECT id FROM learning_objectives WHERE skill_id=:skillId AND code=:code").param("skillId",skillId).param("code",code).query(UUID.class).optional().orElse(UUID.randomUUID());
        Instant now=Instant.now();
        jdbcClient.sql("INSERT INTO learning_objectives(id,skill_id,code,description,complexity_level,depth_level,display_order,status,created_at,updated_at) VALUES(:id,:skillId,:code,:description,:complexity,:depth,:order,'ACTIVE',:now,:now) ON CONFLICT(skill_id,code) DO UPDATE SET description=EXCLUDED.description, complexity_level=EXCLUDED.complexity_level, depth_level=EXCLUDED.depth_level, display_order=EXCLUDED.display_order, status='ACTIVE', updated_at=EXCLUDED.updated_at")
                .param("id",id).param("skillId",skillId).param("code",code).param("description",description).param("complexity",complexity.name()).param("depth",depth.name()).param("order",order).param("now",Timestamp.from(now)).update();
        return id;
    }
}
