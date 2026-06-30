package com.boom.parentdashboard.api.dto;

import java.time.LocalDate;
import java.util.List;

public record ParentDashboardResponse(
        StudentSummary student,
        WeeklySummary weeklySummary,
        SelectedPeriod selectedPeriod,
        List<MetricSummary> metrics,
        List<ActivityHistoryItem> activityHistory,
        List<SubjectPerformance> subjectPerformance,
        List<LearningGap> learningGaps,
        CurrentActionPlan currentActionPlan,
        List<RecentActivitySummary> recentActivitySummaries,
        Object emptyState
) {
    public record StudentSummary(
            String id,
            String displayName,
            String gradeLevel,
            String targetSchoolSystem
    ) {
    }

    public record WeeklySummary(
            int completedActivities,
            int totalStudyTimeMinutes,
            int averageAccuracy,
            int currentStreakDays
    ) {
    }

    public record SelectedPeriod(
            DashboardPeriodPreset preset,
            LocalDate startDate,
            LocalDate endDate,
            LocalDate comparisonStartDate,
            LocalDate comparisonEndDate,
            String label,
            String comparisonLabel
    ) {
    }

    public record MetricSummary(
            String id,
            String label,
            String value,
            String helperText,
            TrendDirection trendDirection,
            String trendLabel,
            Double trendPercent,
            boolean higherIsBetter
    ) {
    }

    public record ActivityHistoryItem(
            LocalDate date,
            int completedActivities,
            Integer accuracy,
            int studyTimeMinutes
    ) {
    }

    public record SubjectPerformance(
            String subjectId,
            String subjectName,
            int accuracy,
            int studyTimeMinutes,
            String trend,
            int activeGapCount
    ) {
    }

    public record LearningGap(
            String gapId,
            String subjectName,
            String topicName,
            String skillName,
            String severity,
            String status,
            int daysOpen,
            int practiceTimeMinutes
    ) {
    }

    public record CurrentActionPlan(
            String actionPlanId,
            String title,
            String description,
            String targetSubject,
            String targetTopic,
            String targetSkill,
            String priority,
            int estimatedEffortMinutes,
            int progressPercentage,
            List<ActionPlanItem> items
    ) {
    }

    public record ActionPlanItem(
            String title,
            int expectedDurationMinutes,
            String status
    ) {
    }

    public record RecentActivitySummary(
            String activityId,
            LocalDate date,
            String activityTitle,
            String subjectName,
            int accuracy,
            int durationMinutes,
            String summaryText
    ) {
    }
}
