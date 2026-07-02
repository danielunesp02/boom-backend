package com.boom.student.learningevent.application;

import com.boom.student.learningevent.domain.StudentLearningEventType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class StudentLearningEventServiceDateTest {

    @Test
    void commandShouldCarryCoreEventContext() {
        StudentLearningEventCommand command = new StudentLearningEventCommand(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                StudentLearningEventType.QUESTION_ANSWERED,
                null,
                true,
                BigDecimal.ONE,
                15,
                1,
                "WEB",
                "en-US",
                "{\"source\":\"test\"}"
        );

        assertThat(command.eventType()).isEqualTo(StudentLearningEventType.QUESTION_ANSWERED);
        assertThat(command.correct()).isTrue();
        assertThat(command.score()).isEqualByComparingTo(BigDecimal.ONE);
        assertThat(command.timeSpentSeconds()).isEqualTo(15);
        assertThat(command.sourceChannel()).isEqualTo("WEB");
        assertThat(command.locale()).isEqualTo("en-US");
    }
}
