package com.boom.learning.application;

import com.boom.learning.api.dto.LearningActivityDetailResponse;
import com.boom.learning.api.dto.LearningActivitySummaryResponse;
import com.boom.learning.repository.LearningActivityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class LearningActivitiesSeedServiceTest {

    @Autowired
    private LearningActivitiesSeedService seedService;

    @Autowired
    private LearningActivityRepository repository;

    @Autowired
    private LearningActivityService activityService;

    @Test
    void shouldSeedLearningActivitiesQuestionsAndOptions() {
        LearningActivitiesSeedService.SeedResult result = seedService.seed();

        assertThat(result.activities()).isEqualTo(2);
        assertThat(result.questions()).isEqualTo(3);
        assertThat(result.options()).isEqualTo(12);

        List<LearningActivitySummaryResponse> activities = activityService.listActivities(null, null, null, null);

        assertThat(activities)
                .extracting(LearningActivitySummaryResponse::code)
                .contains(
                        "MATH_FRACTIONS_VISUAL_EQUIVALENCE_PRACTICE",
                        "ENG_READING_MAIN_IDEA_SHORT_TEXT"
                );

        LearningActivityDetailResponse detail = activityService.getActivity(result.references().get("fractionsActivity"));

        assertThat(detail.questions()).hasSize(2);
        assertThat(detail.questions().get(0).options()).hasSize(4);
    }

    @Test
    void shouldBeIdempotent() {
        seedService.seed();
        seedService.seed();

        List<LearningActivitySummaryResponse> activities = activityService.listActivities(null, null, null, null);

        assertThat(activities)
                .extracting(LearningActivitySummaryResponse::code)
                .containsOnlyOnce(
                        "MATH_FRACTIONS_VISUAL_EQUIVALENCE_PRACTICE",
                        "ENG_READING_MAIN_IDEA_SHORT_TEXT"
                );
    }
}
