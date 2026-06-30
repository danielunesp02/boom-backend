package com.boom.learning.application;

import com.boom.learning.domain.*;
import com.boom.learning.repository.CurriculumRepository;
import com.boom.learning.repository.LearningRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class LearningTaxonomySeedService {
    private final LearningRepository learning;
    private final CurriculumRepository curriculum;
    public LearningTaxonomySeedService(LearningRepository learning, CurriculumRepository curriculum) { this.learning=learning; this.curriculum=curriculum; }

    @Transactional
    public SeedResult seed() {
        UUID math=learning.upsertSubject("MATHEMATICS","Mathematics","Numbers, reasoning, geometry, algebra, statistics, and problem solving.");
        UUID english=learning.upsertSubject("ENGLISH","English","Reading comprehension, vocabulary, writing, grammar, and communication.");
        UUID science=learning.upsertSubject("SCIENCE","Science","Scientific inquiry, natural phenomena, evidence, charts, and systems thinking.");
        UUID literature=learning.upsertSubject("LITERATURE","Literature","Literary interpretation, authors, historical context, narrative structures, and exam preparation.");
        UUID fractions=learning.upsertTopic(math,"FRACTIONS","Fractions","Represent, compare, simplify, and apply fractions.",10);
        UUID reading=learning.upsertTopic(english,"READING_COMPREHENSION","Reading comprehension","Understand main ideas, infer meaning, and identify supporting evidence.",10);
        UUID charts=learning.upsertTopic(science,"SCIENTIFIC_CHARTS","Scientific charts","Read, interpret, and explain data in charts and diagrams.",10);
        UUID brlit=learning.upsertTopic(literature,"BRAZILIAN_LITERATURE","Brazilian literature","Brazilian literary works, movements, authors, and vestibular-oriented interpretation.",10);
        UUID eq=learning.upsertSkill(fractions,"IDENTIFY_EQUIVALENT_FRACTIONS","Identify equivalent fractions","Recognize equivalent fractions using visual, numeric, and simplification strategies.",10);
        UUID main=learning.upsertSkill(reading,"IDENTIFY_MAIN_IDEA","Identify the main idea","Find the central idea and distinguish it from supporting details.",10);
        UUID ev=learning.upsertSkill(charts,"INTERPRET_CHART_EVIDENCE","Interpret chart evidence","Use charts and data to support scientific conclusions.",10);
        UUID narrator=learning.upsertSkill(brlit,"IDENTIFY_UNRELIABLE_NARRATOR","Identify unreliable narrator","Analyze narrator perspective, contradictions, and textual ambiguity.",10);
        UUID obj1=learning.upsertObjective(eq,"VISUAL_EQUIVALENCE","Identify equivalent fractions using visual representations.",ComplexityLevel.UNDERSTAND,DepthLevel.FOUNDATIONAL,10);
        UUID obj2=learning.upsertObjective(eq,"SIMPLIFY_AND_COMPARE","Simplify and compare fractions to determine equivalence.",ComplexityLevel.APPLY,DepthLevel.STANDARD,20);
        UUID obj3=learning.upsertObjective(main,"MAIN_IDEA_SHORT_TEXT","Identify the main idea in a short text and select supporting evidence.",ComplexityLevel.UNDERSTAND,DepthLevel.STANDARD,10);
        UUID obj4=learning.upsertObjective(ev,"CHART_TO_CONCLUSION","Interpret a chart and choose the conclusion best supported by the evidence.",ComplexityLevel.ANALYZE,DepthLevel.STANDARD,10);
        UUID obj5=learning.upsertObjective(narrator,"NARRATOR_BIAS_AND_AMBIGUITY","Identify narrative bias and ambiguity in a literary passage.",ComplexityLevel.ANALYZE,DepthLevel.DEEP,10);
        UUID it=curriculum.upsertFramework("IT","ITALY_LOWER_SECONDARY","Italy - Lower Secondary School","internal-v1",CurriculumSourceType.INTERNAL);
        UUID br=curriculum.upsertFramework("BR","BRAZIL_BNCC","Brazil - BNCC","internal-v1",CurriculumSourceType.INTERNAL);
        UUID itBand=curriculum.upsertBand(it,"AGE_11_12_GRADE_7",132,155,"GRADE_7","LOWER_SECONDARY",10);
        UUID brBand=curriculum.upsertBand(br,"AGE_11_12_GRADE_7",132,155,"GRADE_7","ENSINO_FUNDAMENTAL_II",10);
        for(UUID band: List.of(itBand, brBand)) {
            curriculum.upsertExpectation(band,math,fractions,eq,obj1,KnowledgeLevel.DEVELOPING,ComplexityLevel.UNDERSTAND,DepthLevel.FOUNDATIONAL,CurriculumPriority.CORE);
            curriculum.upsertExpectation(band,math,fractions,eq,obj2,KnowledgeLevel.DEVELOPING,ComplexityLevel.APPLY,DepthLevel.STANDARD,CurriculumPriority.CORE);
            curriculum.upsertExpectation(band,english,reading,main,obj3,KnowledgeLevel.PROFICIENT,ComplexityLevel.UNDERSTAND,DepthLevel.STANDARD,CurriculumPriority.CORE);
            curriculum.upsertExpectation(band,science,charts,ev,obj4,KnowledgeLevel.DEVELOPING,ComplexityLevel.ANALYZE,DepthLevel.STANDARD,CurriculumPriority.RECOMMENDED);
            curriculum.upsertExpectation(band,literature,brlit,narrator,obj5,KnowledgeLevel.INTRODUCED,ComplexityLevel.ANALYZE,DepthLevel.DEEP,CurriculumPriority.RECOMMENDED);
        }
        return new SeedResult(4,4,4,5,2,2,10,Map.of("mathematics",math,"literature",literature,"italyFramework",it,"brazilFramework",br));
    }
    public record SeedResult(int subjects,int topics,int skills,int objectives,int frameworks,int bands,int expectations,Map<String,UUID> references) {}
}
