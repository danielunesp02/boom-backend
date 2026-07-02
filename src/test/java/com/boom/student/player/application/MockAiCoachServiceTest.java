package com.boom.student.player.application;

import com.boom.learning.domain.ActivityQuestion;
import com.boom.learning.domain.ComplexityLevel;
import com.boom.learning.domain.DepthLevel;
import com.boom.learning.domain.LearningActivity;
import com.boom.learning.domain.LearningActivityType;
import com.boom.learning.domain.LearningStatus;
import com.boom.learning.domain.QuestionType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MockAiCoachServiceTest {

    private final MockAiCoachService service = new MockAiCoachService();

    @Test
    void shouldCreateFriendlyIntroAndHint() {
        LearningActivity activity = new LearningActivity(
                UUID.randomUUID(),
                "MATH_FRACTIONS_VISUAL_EQUIVALENCE_PRACTICE",
                "Visual fraction comparison practice",
                "Practice equivalent fractions.",
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                null,
                null,
                null,
                LearningActivityType.PRACTICE,
                20,
                ComplexityLevel.UNDERSTAND,
                DepthLevel.FOUNDATIONAL,
                10,
                LearningStatus.ACTIVE,
                Instant.now(),
                Instant.now()
        );

        ActivityQuestion question = new ActivityQuestion(
                UUID.randomUUID(),
                activity.id(),
                "Q1",
                "Which fraction is equivalent to 1/2?",
                "2/4 is equivalent to 1/2.",
                QuestionType.MULTIPLE_CHOICE,
                activity.subjectId(),
                activity.topicId(),
                activity.skillId(),
                activity.objectiveId(),
                ComplexityLevel.UNDERSTAND,
                DepthLevel.FOUNDATIONAL,
                10,
                LearningStatus.ACTIVE,
                Instant.now(),
                Instant.now()
        );

        assertThat(service.activityIntro(activity, "Helena").message()).contains("Helena");
        assertThat(service.questionHint(activity, question).message()).isNotBlank();
        assertThat(service.completionPreview(activity, "Helena").message()).contains("terminar");
    }
}
