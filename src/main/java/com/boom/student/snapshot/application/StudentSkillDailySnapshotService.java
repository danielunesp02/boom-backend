package com.boom.student.snapshot.application;

import com.boom.student.snapshot.api.dto.SnapshotRebuildResponse;
import com.boom.student.snapshot.api.dto.StudentSkillDailySnapshotResponse;
import com.boom.student.snapshot.domain.StudentSkillDailySnapshot;
import com.boom.student.snapshot.repository.StudentSkillDailySnapshotRepository;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class StudentSkillDailySnapshotService {

    private final JdbcClient jdbcClient;
    private final StudentSkillDailySnapshotRepository repository;

    public StudentSkillDailySnapshotService(JdbcClient jdbcClient, StudentSkillDailySnapshotRepository repository) {
        this.jdbcClient = jdbcClient;
        this.repository = repository;
    }

    public SnapshotRebuildResponse rebuildDailySnapshots(String dateValue) {
        LocalDate snapshotDate = parseDateOrToday(dateValue);
        int rebuilt = repository.rebuildForDate(snapshotDate);
        return new SnapshotRebuildResponse(snapshotDate.toString(), rebuilt);
    }

    public List<StudentSkillDailySnapshotResponse> getStudentSkillSnapshots(
            String studentIdValue,
            String dateValue,
            Authentication authentication
    ) {
        UUID studentId = parseUuid(studentIdValue, "Invalid studentId.");
        UUID guardianId = authenticatedGuardianId(authentication);
        ensureGuardianCanAccessStudent(guardianId, studentId);
        LocalDate snapshotDate = parseDateOrToday(dateValue);

        return repository.findByStudentAndDate(studentId, snapshotDate).stream()
                .map(this::toResponse)
                .toList();
    }

    private StudentSkillDailySnapshotResponse toResponse(StudentSkillDailySnapshot s) {
        return new StudentSkillDailySnapshotResponse(
                s.id().toString(),
                s.snapshotDate().toString(),
                s.studentId().toString(),
                s.guardianId() == null ? null : s.guardianId().toString(),
                s.subjectId().toString(),
                s.topicId().toString(),
                s.skillId().toString(),
                s.gradeLevel(),
                s.targetSchoolSystem(),
                s.countryCode(),
                s.locale(),
                s.questionsAnswered(),
                s.correctAnswers(),
                s.incorrectAnswers(),
                decimal(s.accuracy()),
                decimal(s.totalScore()),
                decimal(s.averageScore()),
                s.totalTimeSpentSeconds(),
                decimal(s.averageTimeSpentSeconds()),
                s.activitiesStarted(),
                s.activitiesCompleted(),
                s.attemptsCompleted(),
                s.firstEventAt() == null ? null : s.firstEventAt().toString(),
                s.lastEventAt() == null ? null : s.lastEventAt().toString()
        );
    }

    private void ensureGuardianCanAccessStudent(UUID guardianId, UUID studentId) {
        Integer count = jdbcClient.sql("""
                SELECT COUNT(*)
                FROM guardian_student_relationships
                WHERE guardian_id = :guardianId
                  AND student_id = :studentId
                """)
                .param("guardianId", guardianId)
                .param("studentId", studentId)
                .query(Integer.class)
                .single();

        if (count == null || count == 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Guardian cannot access this student.");
        }
    }

    private UUID authenticatedGuardianId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required.");
        }

        Object principal = authentication.getPrincipal();

        try {
            Method method = principal.getClass().getMethod("guardianAccountId");
            Object value = method.invoke(principal);

            if (value instanceof UUID guardianId) return guardianId;
            if (value instanceof String guardianIdValue) return UUID.fromString(guardianIdValue);
        } catch (Exception ignored) {
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated guardian not found.");
    }

    private LocalDate parseDateOrToday(String dateValue) {
        if (dateValue == null || dateValue.isBlank()) return LocalDate.now(ZoneOffset.UTC);

        try {
            return LocalDate.parse(dateValue);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date. Expected YYYY-MM-DD.");
        }
    }

    private UUID parseUuid(String value, String message) {
        try {
            return UUID.fromString(value);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private double decimal(BigDecimal value) {
        return value == null ? 0.0 : value.doubleValue();
    }
}
