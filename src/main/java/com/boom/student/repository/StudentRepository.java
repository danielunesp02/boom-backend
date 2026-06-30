package com.boom.student.repository;

import com.boom.student.domain.GradeLevel;
import com.boom.student.domain.Student;
import com.boom.student.domain.StudentStatus;
import com.boom.student.domain.TargetSchoolSystem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public class StudentRepository {

    private final JdbcTemplate jdbcTemplate;

    public StudentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Student student) {
        jdbcTemplate.update(
                """
                INSERT INTO students (
                    id, display_name, birth_date, grade_level, target_school_system,
                    preferred_locale, status, created_at, updated_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                student.id(),
                student.displayName(),
                student.birthDate() == null ? null : Date.valueOf(student.birthDate()),
                student.gradeLevel().name(),
                student.targetSchoolSystem().name(),
                student.preferredLocale(),
                student.status().name(),
                Timestamp.from(student.createdAt()),
                Timestamp.from(student.updatedAt())
        );
    }

    public Optional<Student> findById(UUID id) {
        return jdbcTemplate.query(
                """
                SELECT id, display_name, birth_date, grade_level, target_school_system,
                       preferred_locale, status, created_at, updated_at
                FROM students
                WHERE id = ?
                """,
                (rs, rowNum) -> mapStudent(rs),
                id
        ).stream().findFirst();
    }

    private Student mapStudent(ResultSet rs) throws java.sql.SQLException {
        Date birthDate = rs.getDate("birth_date");
        return new Student(
                rs.getObject("id", UUID.class),
                rs.getString("display_name"),
                birthDate == null ? null : birthDate.toLocalDate(),
                GradeLevel.valueOf(rs.getString("grade_level")),
                TargetSchoolSystem.valueOf(rs.getString("target_school_system")),
                rs.getString("preferred_locale"),
                StudentStatus.valueOf(rs.getString("status")),
                toInstant(rs.getTimestamp("created_at")),
                toInstant(rs.getTimestamp("updated_at"))
        );
    }

    private Instant toInstant(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant();
    }
}
