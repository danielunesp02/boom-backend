package com.boom.learning.application;

import com.boom.learning.domain.*;
import com.boom.learning.repository.LearningActivityRepository;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
public class LearningActivitiesSeedService {

    private final LearningTaxonomySeedService taxonomySeedService;
    private final LearningActivityRepository activityRepository;
    private final JdbcClient jdbcClient;

    public LearningActivitiesSeedService(
            LearningTaxonomySeedService taxonomySeedService,
            LearningActivityRepository activityRepository,
            JdbcClient jdbcClient
    ) {
        this.taxonomySeedService = taxonomySeedService;
        this.activityRepository = activityRepository;
        this.jdbcClient = jdbcClient;
    }

    @Transactional
    public SeedResult seed() {
        taxonomySeedService.seed();

        UUID math = findSubject("MATHEMATICS");
        UUID fractions = findTopic(math, "FRACTIONS");
        UUID equivalentFractions = findSkill(fractions, "IDENTIFY_EQUIVALENT_FRACTIONS");
        UUID visualObjective = findObjective(equivalentFractions, "VISUAL_EQUIVALENCE");

        UUID english = findSubject("ENGLISH");
        UUID reading = findTopic(english, "READING_COMPREHENSION");
        UUID mainIdea = findSkill(reading, "IDENTIFY_MAIN_IDEA");
        UUID mainIdeaObjective = findObjective(mainIdea, "MAIN_IDEA_SHORT_TEXT");

        UUID activityFractions = activityRepository.upsertActivity(
                "MATH_FRACTIONS_VISUAL_EQUIVALENCE_PRACTICE",
                "Visual fraction comparison practice",
                "Practice identifying equivalent fractions using visual and numeric representations.",
                math,
                fractions,
                equivalentFractions,
                visualObjective,
                null,
                null,
                null,
                LearningActivityType.PRACTICE,
                20,
                ComplexityLevel.UNDERSTAND,
                DepthLevel.FOUNDATIONAL,
                10
        );

        UUID q1 = activityRepository.upsertQuestion(
                activityFractions,
                "Q1_EQUIVALENT_HALF",
                "Which fraction is equivalent to 1/2?",
                "2/4 is equivalent to 1/2 because both represent the same proportion.",
                QuestionType.MULTIPLE_CHOICE,
                math,
                fractions,
                equivalentFractions,
                visualObjective,
                ComplexityLevel.UNDERSTAND,
                DepthLevel.FOUNDATIONAL,
                10
        );

        seedOptions(q1, new OptionSeed[]{
                new OptionSeed("A", "2/4", true, 10),
                new OptionSeed("B", "2/3", false, 20),
                new OptionSeed("C", "3/5", false, 30),
                new OptionSeed("D", "4/6", false, 40)
        });

        UUID q2 = activityRepository.upsertQuestion(
                activityFractions,
                "Q2_EQUIVALENT_THREE_SIXTHS",
                "Which fraction is equivalent to 3/6?",
                "3/6 simplifies to 1/2, so 1/2 is equivalent.",
                QuestionType.MULTIPLE_CHOICE,
                math,
                fractions,
                equivalentFractions,
                visualObjective,
                ComplexityLevel.APPLY,
                DepthLevel.STANDARD,
                20
        );

        seedOptions(q2, new OptionSeed[]{
                new OptionSeed("A", "1/3", false, 10),
                new OptionSeed("B", "1/2", true, 20),
                new OptionSeed("C", "2/3", false, 30),
                new OptionSeed("D", "3/4", false, 40)
        });

        UUID activityReading = activityRepository.upsertActivity(
                "ENG_READING_MAIN_IDEA_SHORT_TEXT",
                "Main idea short text practice",
                "Practice identifying the central idea and supporting evidence in a short text.",
                english,
                reading,
                mainIdea,
                mainIdeaObjective,
                null,
                null,
                null,
                LearningActivityType.PRACTICE,
                15,
                ComplexityLevel.UNDERSTAND,
                DepthLevel.STANDARD,
                20
        );

        UUID q3 = activityRepository.upsertQuestion(
                activityReading,
                "Q1_MAIN_IDEA",
                "A short text explains that bees help plants reproduce by carrying pollen from flower to flower. What is the main idea?",
                "The text mainly explains how bees help plants reproduce.",
                QuestionType.MULTIPLE_CHOICE,
                english,
                reading,
                mainIdea,
                mainIdeaObjective,
                ComplexityLevel.UNDERSTAND,
                DepthLevel.STANDARD,
                10
        );

        seedOptions(q3, new OptionSeed[]{
                new OptionSeed("A", "Bees are dangerous insects.", false, 10),
                new OptionSeed("B", "Bees help plants reproduce.", true, 20),
                new OptionSeed("C", "Flowers have different colors.", false, 30),
                new OptionSeed("D", "Pollen is always yellow.", false, 40)
        });

        return new SeedResult(
                2,
                3,
                12,
                Map.of(
                        "fractionsActivity", activityFractions,
                        "readingActivity", activityReading
                )
        );
    }

    private void seedOptions(UUID questionId, OptionSeed[] options) {
        for (OptionSeed option : options) {
            activityRepository.upsertOption(
                    questionId,
                    option.label(),
                    option.text(),
                    option.correct(),
                    option.displayOrder()
            );
        }
    }

    private UUID findSubject(String code) {
        return jdbcClient.sql("SELECT id FROM learning_subjects WHERE code = :code")
                .param("code", code)
                .query(UUID.class)
                .single();
    }

    private UUID findTopic(UUID subjectId, String code) {
        return jdbcClient.sql("SELECT id FROM learning_topics WHERE subject_id = :subjectId AND code = :code")
                .param("subjectId", subjectId)
                .param("code", code)
                .query(UUID.class)
                .single();
    }

    private UUID findSkill(UUID topicId, String code) {
        return jdbcClient.sql("SELECT id FROM learning_skills WHERE topic_id = :topicId AND code = :code")
                .param("topicId", topicId)
                .param("code", code)
                .query(UUID.class)
                .single();
    }

    private UUID findObjective(UUID skillId, String code) {
        return jdbcClient.sql("SELECT id FROM learning_objectives WHERE skill_id = :skillId AND code = :code")
                .param("skillId", skillId)
                .param("code", code)
                .query(UUID.class)
                .single();
    }

    private record OptionSeed(String label, String text, boolean correct, int displayOrder) {
    }

    public record SeedResult(
            int activities,
            int questions,
            int options,
            Map<String, UUID> references
    ) {
    }
}
