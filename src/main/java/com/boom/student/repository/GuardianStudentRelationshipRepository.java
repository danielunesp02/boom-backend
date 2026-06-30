package com.boom.student.repository;

import com.boom.student.api.dto.ParentStudentResponse;
import com.boom.student.domain.GuardianStudentRelationship;
import com.boom.student.domain.GuardianStudentRelationshipStatus;
import com.boom.student.domain.RelationshipType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class GuardianStudentRelationshipRepository {

    private final JdbcTemplate jdbcTemplate;

    public GuardianStudentRelationshipRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(GuardianStudentRelationship relationship) {
        jdbcTemplate.update(
                """
                INSERT INTO guardian_student_relationships (
                    id, guardian_id, student_id, relationship_type, is_primary,
                    status, created_at, updated_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                relationship.id(), relationship.guardianId(), relationship.studentId(),
                relationship.relationshipType().name(), relationship.primary(), relationship.status().name(),
                Timestamp.from(relationship.createdAt()), Timestamp.from(relationship.updatedAt())
        );
    }

    public boolean existsActiveRelationship(UUID guardianId, UUID studentId) {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM guardian_student_relationships
                WHERE guardian_id = ? AND student_id = ? AND status = 'ACTIVE'
                """,
                Integer.class, guardianId, studentId
        );
        return count != null && count > 0;
    }

    public Optional<GuardianStudentRelationship> findPrimaryActiveRelationship(UUID guardianId) {
        return jdbcTemplate.query(
                """
                SELECT id, guardian_id, student_id, relationship_type, is_primary,
                       status, created_at, updated_at
                FROM guardian_student_relationships
                WHERE guardian_id = ? AND status = 'ACTIVE' AND is_primary = true
                ORDER BY created_at ASC
                LIMIT 1
                """,
                (rs, rowNum) -> mapRelationship(rs), guardianId
        ).stream().findFirst();
    }

    public List<ParentStudentResponse> listStudentsForGuardian(UUID guardianId) {
        return jdbcTemplate.query(
                """
                SELECT s.id AS student_id, s.display_name, s.grade_level, s.target_school_system,
                       s.preferred_locale, r.relationship_type, r.is_primary
                FROM guardian_student_relationships r
                JOIN students s ON s.id = r.student_id
                WHERE r.guardian_id = ? AND r.status = 'ACTIVE' AND s.status = 'ACTIVE'
                ORDER BY r.is_primary DESC, s.display_name ASC
                """,
                (rs, rowNum) -> new ParentStudentResponse(
                        rs.getObject("student_id", UUID.class),
                        rs.getString("display_name"),
                        rs.getString("grade_level"),
                        rs.getString("target_school_system"),
                        rs.getString("preferred_locale"),
                        rs.getString("relationship_type"),
                        rs.getBoolean("is_primary")
                ),
                guardianId
        );
    }

    private GuardianStudentRelationship mapRelationship(ResultSet rs) throws java.sql.SQLException {
        return new GuardianStudentRelationship(
                rs.getObject("id", UUID.class),
                rs.getObject("guardian_id", UUID.class),
                rs.getObject("student_id", UUID.class),
                RelationshipType.valueOf(rs.getString("relationship_type")),
                rs.getBoolean("is_primary"),
                GuardianStudentRelationshipStatus.valueOf(rs.getString("status")),
                toInstant(rs.getTimestamp("created_at")),
                toInstant(rs.getTimestamp("updated_at"))
        );
    }

    private Instant toInstant(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant();
    }
}
