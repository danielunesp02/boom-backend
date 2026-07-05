package com.boom.parentdashboard.realdata;

import java.util.List;
import java.util.UUID;

public record ParentDashboardRealMetrics(
        ParentDashboardRealDataStatus status,
        UUID studentId,
        String dateFrom,
        String dateTo,
        int questionsAnswered,
        int correctAnswers,
        int incorrectAnswers,
        double accuracy,
        int totalTimeSpentSeconds,
        int completedActivities,
        List<ParentDashboardRealMetrics.DailyActivityMetric> dailyActivityMetrics,
        List<ParentDashboardRealMetrics.RecentActivityMetric> recentActivityMetrics,
        List<ParentDashboardRealMetrics.LearningGapMetric> learningGapMetrics,
        List<ParentDashboardRealMetrics.SubjectMetric> subjectMetrics,
        List<ParentDashboardRealMetrics.SkillMetric> skillMetrics
) {

    public boolean hasRealData() {
        return status == ParentDashboardRealDataStatus.REAL_DATA_AVAILABLE;
    }

    public record DailyActivityMetric(String snapshotDate, int completedActivities, Integer accuracy, int totalTimeSpentSeconds) {
    }

    public record RecentActivityMetric(
            String activityId,
            String completedDate,
            String activityTitle,
            String subjectName,
            int accuracy,
            int durationSeconds,
            int correctAnswers,
            int answeredQuestions
    ) {
    }

    public record LearningGapMetric(
            UUID subjectId,
            String subjectName,
            UUID topicId,
            String topicName,
            UUID skillId,
            String skillName,
            String masteryStatus,
            int accuracy,
            int masteryScore,
            String priority,
            String reviewType,
            String reviewStatus,
            String reason,
            String nextReviewDate,
            boolean requiresReassessment,
            int overdueDays
    ) {
    }

    public record SubjectMetric(
            UUID subjectId,
            String subjectName,
            int questionsAnswered,
            int correctAnswers,
            double accuracy,
            int totalTimeSpentSeconds,
            int completedActivities
    ) {
    }

    public record SkillMetric(
            UUID subjectId,
            String subjectName,
            UUID topicId,
            String topicName,
            UUID skillId,
            String skillName,
            int questionsAnswered,
            int correctAnswers,
            int incorrectAnswers,
            double accuracy,
            int totalTimeSpentSeconds,
            int completedActivities
    ) {
    }
}
