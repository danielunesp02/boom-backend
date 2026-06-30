package com.boom.parentdashboard.api;

import com.boom.parentdashboard.application.ParentDashboardMockService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ParentDashboardController.class)
@Import(ParentDashboardMockService.class)
class ParentDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnParentDashboardMockDataInEnglishByDefault() throws Exception {
        mockMvc.perform(get("/api/v1/parents/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.student.displayName").value("Helena"))
                .andExpect(jsonPath("$.weeklySummary.completedActivities").value(5))
                .andExpect(jsonPath("$.activityHistory", hasSize(7)))
                .andExpect(jsonPath("$.subjectPerformance[0].subjectName").value("Mathematics"))
                .andExpect(jsonPath("$.learningGaps[0].topicName").value("Fractions"))
                .andExpect(jsonPath("$.learningGaps[0].skillName").value("Equivalent fractions"))
                .andExpect(jsonPath("$.currentActionPlan.title").value("Review equivalent fractions"));
    }

    @Test
    void shouldReturnParentDashboardDomainContentInPortuguese() throws Exception {
        mockMvc.perform(get("/api/v1/parents/dashboard")
                        .header("X-Boom-Locale", "pt-BR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subjectPerformance[0].subjectName").value("Matemática"))
                .andExpect(jsonPath("$.learningGaps[0].topicName").value("Frações"))
                .andExpect(jsonPath("$.learningGaps[0].skillName").value("Frações equivalentes"))
                .andExpect(jsonPath("$.currentActionPlan.title").value("Revisar frações equivalentes"));
    }

    @Test
    void shouldResolveLocaleFromAcceptLanguageHeader() throws Exception {
        mockMvc.perform(get("/api/v1/parents/dashboard")
                        .header("Accept-Language", "it-IT,it;q=0.9,en;q=0.8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subjectPerformance[0].subjectName").value("Matematica"))
                .andExpect(jsonPath("$.learningGaps[0].topicName").value("Frazioni"))
                .andExpect(jsonPath("$.currentActionPlan.title").value("Ripassare le frazioni equivalenti"));
    }
}
