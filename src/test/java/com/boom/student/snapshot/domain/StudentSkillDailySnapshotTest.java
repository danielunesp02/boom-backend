package com.boom.student.snapshot.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class StudentSkillDailySnapshotTest {

    @Test
    void shouldCarryDailySkillMetrics() {
        StudentSkillDailySnapshot snapshot = new StudentSkillDailySnapshot(
                UUID.randomUUID(),
                LocalDate.parse("2026-07-02"),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "GRADE_7",
                "ITALY",
                null,
                "en-US",
                2,
                2,
                0,
                new BigDecimal("100.0000"),
                new BigDecimal("2.0000"),
                new BigDecimal("1.0000"),
                24,
                new BigDecimal("12.00"),
                1,
                1,
                1,
                Instant.now(),
                Instant.now(),
                Instant.now(),
                Instant.now()
        );

        assertThat(snapshot.questionsAnswered()).isEqualTo(2);
        assertThat(snapshot.correctAnswers()).isEqualTo(2);
        assertThat(snapshot.accuracy()).isEqualByComparingTo("100.0000");
    }
}
