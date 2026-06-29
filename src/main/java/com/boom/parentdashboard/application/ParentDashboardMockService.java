package com.boom.parentdashboard.application;

import com.boom.parentdashboard.api.dto.ParentDashboardResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParentDashboardMockService {

    public ParentDashboardResponse getDashboard() {
        return new ParentDashboardResponse(
                student(),
                weeklySummary(),
                activityHistory(),
                subjectPerformance(),
                learningGaps(),
                currentActionPlan(),
                recentActivitySummaries(),
                null
        );
    }

    private ParentDashboardResponse.Student student() {
        return new ParentDashboardResponse.Student(
                "student-helena",
                "Helena",
                "Lower Secondary",
                "Italy - Scuola Secondaria di Primo Grado"
        );
    }

    private ParentDashboardResponse.WeeklySummary weeklySummary() {
        return new ParentDashboardResponse.WeeklySummary(
                5,
                130,
                76,
                3
        );
    }

    private List<ParentDashboardResponse.ActivityHistoryPoint> activityHistory() {
        return List.of(
                new ParentDashboardResponse.ActivityHistoryPoint("2026-06-23", 1, 68, 20),
                new ParentDashboardResponse.ActivityHistoryPoint("2026-06-24", 1, 72, 25),
                new ParentDashboardResponse.ActivityHistoryPoint("2026-06-25", 0, null, 0),
                new ParentDashboardResponse.ActivityHistoryPoint("2026-06-26", 1, 76, 30),
                new ParentDashboardResponse.ActivityHistoryPoint("2026-06-27", 1, 82, 25),
                new ParentDashboardResponse.ActivityHistoryPoint("2026-06-28", 1, 81, 30),
                new ParentDashboardResponse.ActivityHistoryPoint("2026-06-29", 0, null, 0)
        );
    }

    private List<ParentDashboardResponse.SubjectPerformance> subjectPerformance() {
        return List.of(
                new ParentDashboardResponse.SubjectPerformance("math", "Mathematics", 68, 55, "IMPROVING", 2),
                new ParentDashboardResponse.SubjectPerformance("english", "English", 84, 35, "STABLE", 0),
                new ParentDashboardResponse.SubjectPerformance("science", "Science", 73, 40, "IMPROVING", 1)
        );
    }

    private List<ParentDashboardResponse.LearningGap> learningGaps() {
        return List.of(
                new ParentDashboardResponse.LearningGap(
                        "gap-fractions",
                        "Mathematics",
                        "Fractions",
                        "Equivalent fractions",
                        "MEDIUM",
                        "IN_PROGRESS",
                        6,
                        45
                ),
                new ParentDashboardResponse.LearningGap(
                        "gap-science-charts",
                        "Science",
                        "Scientific charts",
                        "Interpret chart evidence",
                        "LOW",
                        "OPEN",
                        2,
                        18
                )
        );
    }

    private ParentDashboardResponse.CurrentActionPlan currentActionPlan() {
        return new ParentDashboardResponse.CurrentActionPlan(
                "plan-fractions",
                "Review equivalent fractions",
                "Practice visual comparison of equivalent fractions before moving to proportional reasoning.",
                "Mathematics",
                "Fractions",
                "Equivalent fractions",
                "MEDIUM",
                45,
                40,
                List.of(
                        new ParentDashboardResponse.ActionPlanItem("Review visual explanation", 10, "COMPLETED"),
                        new ParentDashboardResponse.ActionPlanItem("Practice 10 visual fraction questions", 20, "IN_PROGRESS"),
                        new ParentDashboardResponse.ActionPlanItem("Retry short challenge", 15, "PENDING")
                )
        );
    }

    private List<ParentDashboardResponse.RecentActivitySummary> recentActivitySummaries() {
        return List.of(
                new ParentDashboardResponse.RecentActivitySummary(
                        "act-2026-06-28",
                        "2026-06-28",
                        "Fractions practice",
                        "Mathematics",
                        81,
                        30,
                        "Helena improved in visual comparison but still needs practice recognizing equivalent fractions quickly."
                ),
                new ParentDashboardResponse.RecentActivitySummary(
                        "act-2026-06-27",
                        "2026-06-27",
                        "Reading comprehension",
                        "English",
                        88,
                        25,
                        "Strong performance identifying the main idea and supporting details in short texts."
                )
        );
    }
}
