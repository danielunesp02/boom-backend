package com.boom.learning.repository;

import com.boom.learning.domain.*;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public class CurriculumRepository {
    private final JdbcClient jdbcClient;
    public CurriculumRepository(JdbcClient jdbcClient) { this.jdbcClient = jdbcClient; }

    public List<CurriculumFramework> findActiveFrameworks() {
        return jdbcClient.sql("SELECT id,country_code,code,name,version,source_type,status,created_at,updated_at FROM curriculum_frameworks WHERE status='ACTIVE' ORDER BY country_code,name")
                .query((rs,n)->new CurriculumFramework(rs.getObject("id",UUID.class),rs.getString("country_code"),rs.getString("code"),rs.getString("name"),rs.getString("version"),CurriculumSourceType.valueOf(rs.getString("source_type")),LearningStatus.valueOf(rs.getString("status")),rs.getTimestamp("created_at").toInstant(),rs.getTimestamp("updated_at").toInstant())).list();
    }
    public List<CurriculumBand> findActiveBandsByFramework(UUID frameworkId) {
        return jdbcClient.sql("SELECT id,framework_id,code,min_age_months,max_age_months,grade_level,school_stage,display_order,status,created_at,updated_at FROM curriculum_bands WHERE framework_id=:frameworkId AND status='ACTIVE' ORDER BY display_order")
                .param("frameworkId",frameworkId).query((rs,n)->new CurriculumBand(rs.getObject("id",UUID.class),rs.getObject("framework_id",UUID.class),rs.getString("code"),rs.getInt("min_age_months"),rs.getInt("max_age_months"),rs.getString("grade_level"),rs.getString("school_stage"),rs.getInt("display_order"),LearningStatus.valueOf(rs.getString("status")),rs.getTimestamp("created_at").toInstant(),rs.getTimestamp("updated_at").toInstant())).list();
    }
    public List<CurriculumExpectation> findActiveExpectationsByBand(UUID bandId) {
        return jdbcClient.sql("SELECT id,band_id,subject_id,topic_id,skill_id,objective_id,expected_knowledge_level,expected_complexity_level,expected_depth_level,priority,status,created_at,updated_at FROM curriculum_expectations WHERE band_id=:bandId AND status='ACTIVE' ORDER BY priority, subject_id")
                .param("bandId",bandId).query((rs,n)->new CurriculumExpectation(rs.getObject("id",UUID.class),rs.getObject("band_id",UUID.class),rs.getObject("subject_id",UUID.class),rs.getObject("topic_id",UUID.class),rs.getObject("skill_id",UUID.class),rs.getObject("objective_id",UUID.class),KnowledgeLevel.valueOf(rs.getString("expected_knowledge_level")),ComplexityLevel.valueOf(rs.getString("expected_complexity_level")),DepthLevel.valueOf(rs.getString("expected_depth_level")),CurriculumPriority.valueOf(rs.getString("priority")),LearningStatus.valueOf(rs.getString("status")),rs.getTimestamp("created_at").toInstant(),rs.getTimestamp("updated_at").toInstant())).list();
    }
    public UUID upsertFramework(String country, String code, String name, String version, CurriculumSourceType source) {
        UUID id=jdbcClient.sql("SELECT id FROM curriculum_frameworks WHERE country_code=:country AND code=:code AND version=:version").param("country",country).param("code",code).param("version",version).query(UUID.class).optional().orElse(UUID.randomUUID());
        Instant now=Instant.now();
        jdbcClient.sql("INSERT INTO curriculum_frameworks(id,country_code,code,name,version,source_type,status,created_at,updated_at) VALUES(:id,:country,:code,:name,:version,:source,'ACTIVE',:now,:now) ON CONFLICT(country_code,code,version) DO UPDATE SET name=EXCLUDED.name, source_type=EXCLUDED.source_type, status='ACTIVE', updated_at=EXCLUDED.updated_at")
                .param("id",id).param("country",country).param("code",code).param("name",name).param("version",version).param("source",source.name()).param("now",Timestamp.from(now)).update(); return id;
    }
    public UUID upsertBand(UUID frameworkId, String code, int minAge, int maxAge, String grade, String stage, int order) {
        UUID id=jdbcClient.sql("SELECT id FROM curriculum_bands WHERE framework_id=:frameworkId AND code=:code").param("frameworkId",frameworkId).param("code",code).query(UUID.class).optional().orElse(UUID.randomUUID());
        Instant now=Instant.now();
        jdbcClient.sql("INSERT INTO curriculum_bands(id,framework_id,code,min_age_months,max_age_months,grade_level,school_stage,display_order,status,created_at,updated_at) VALUES(:id,:frameworkId,:code,:minAge,:maxAge,:grade,:stage,:order,'ACTIVE',:now,:now) ON CONFLICT(framework_id,code) DO UPDATE SET min_age_months=EXCLUDED.min_age_months, max_age_months=EXCLUDED.max_age_months, grade_level=EXCLUDED.grade_level, school_stage=EXCLUDED.school_stage, display_order=EXCLUDED.display_order, status='ACTIVE', updated_at=EXCLUDED.updated_at")
                .param("id",id).param("frameworkId",frameworkId).param("code",code).param("minAge",minAge).param("maxAge",maxAge).param("grade",grade).param("stage",stage).param("order",order).param("now",Timestamp.from(now)).update(); return id;
    }
    public UUID upsertExpectation(UUID bandId, UUID subjectId, UUID topicId, UUID skillId, UUID objectiveId, KnowledgeLevel knowledge, ComplexityLevel complexity, DepthLevel depth, CurriculumPriority priority) {
        UUID id=jdbcClient.sql("SELECT id FROM curriculum_expectations WHERE band_id=:bandId AND objective_id=:objectiveId").param("bandId",bandId).param("objectiveId",objectiveId).query(UUID.class).optional().orElse(UUID.randomUUID());
        Instant now=Instant.now();
        jdbcClient.sql("INSERT INTO curriculum_expectations(id,band_id,subject_id,topic_id,skill_id,objective_id,expected_knowledge_level,expected_complexity_level,expected_depth_level,priority,status,created_at,updated_at) VALUES(:id,:bandId,:subjectId,:topicId,:skillId,:objectiveId,:knowledge,:complexity,:depth,:priority,'ACTIVE',:now,:now) ON CONFLICT(band_id,objective_id) DO UPDATE SET subject_id=EXCLUDED.subject_id, topic_id=EXCLUDED.topic_id, skill_id=EXCLUDED.skill_id, expected_knowledge_level=EXCLUDED.expected_knowledge_level, expected_complexity_level=EXCLUDED.expected_complexity_level, expected_depth_level=EXCLUDED.expected_depth_level, priority=EXCLUDED.priority, status='ACTIVE', updated_at=EXCLUDED.updated_at")
                .param("id",id).param("bandId",bandId).param("subjectId",subjectId).param("topicId",topicId).param("skillId",skillId).param("objectiveId",objectiveId).param("knowledge",knowledge.name()).param("complexity",complexity.name()).param("depth",depth.name()).param("priority",priority.name()).param("now",Timestamp.from(now)).update(); return id;
    }
}
