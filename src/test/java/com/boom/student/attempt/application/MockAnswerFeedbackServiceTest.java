package com.boom.student.attempt.application;

import com.boom.student.attempt.api.dto.AiCoachFeedbackResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MockAnswerFeedbackServiceTest {

    private final MockAnswerFeedbackService service = new MockAnswerFeedbackService();

    @Test
    void shouldReturnPositiveFeedbackForCorrectAnswer() {
        AiCoachFeedbackResponse feedback = service.feedback(true, "2/4 is equivalent to 1/2.");

        assertThat(feedback.tone()).isEqualTo("CORRECT");
        assertThat(feedback.message()).contains("Muito bem");
        assertThat(feedback.message()).contains("2/4");
    }

    @Test
    void shouldReturnSupportiveFeedbackForIncorrectAnswer() {
        AiCoachFeedbackResponse feedback = service.feedback(false, "3/6 simplifies to 1/2.");

        assertThat(feedback.tone()).isEqualTo("SUPPORTIVE_REVIEW");
        assertThat(feedback.message()).contains("Boa tentativa");
        assertThat(feedback.message()).contains("3/6");
    }
}
