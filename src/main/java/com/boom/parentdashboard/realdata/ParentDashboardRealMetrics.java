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
        List<SubjectMetric> subjectMetrics,
        List<SkillMetric> skillMetrics
) {
    public boolean hasRealData() {
        return status == ParentDashboardRealDataStatus.REAL_DATA_AVAILABLE;
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
