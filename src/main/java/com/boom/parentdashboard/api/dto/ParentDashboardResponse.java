package com.boom.parentdashboard.api.dto;

import java.util.List;

public record ParentDashboardResponse(
        Student student,
        WeeklySummary weeklySummary,
        List<ActivityHistoryPoint> activityHistory,
        List<SubjectPerformance> subjectPerformance,
        List<LearningGap> learningGaps,
        CurrentActionPlan currentActionPlan,
        List<RecentActivitySummary> recentActivitySummaries,
        EmptyState emptyState
) {
    public record Student(
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

    public record ActivityHistoryPoint(
            String date,
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
            String date,
            String activityTitle,
            String subjectName,
            int accuracy,
            int durationMinutes,
            String summaryText
    ) {
    }

    public record EmptyState(
            String title,
            String description
    ) {
    }
}
