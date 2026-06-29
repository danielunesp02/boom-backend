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
    void shouldReturnParentDashboardMockData() throws Exception {
        mockMvc.perform(get("/api/v1/parents/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.student.displayName").value("Helena"))
                .andExpect(jsonPath("$.weeklySummary.completedActivities").value(5))
                .andExpect(jsonPath("$.activityHistory", hasSize(7)))
                .andExpect(jsonPath("$.subjectPerformance[0].subjectName").value("Mathematics"))
                .andExpect(jsonPath("$.learningGaps[0].skillName").value("Equivalent fractions"))
                .andExpect(jsonPath("$.currentActionPlan.title").value("Review equivalent fractions"));
    }
}
