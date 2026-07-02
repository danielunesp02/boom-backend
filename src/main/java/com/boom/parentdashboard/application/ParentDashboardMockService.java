package com.boom.parentdashboard.application;

import com.boom.parentdashboard.api.dto.ParentDashboardResponse;
import com.boom.parentdashboard.api.dto.TrendDirection;
import com.boom.parentdashboard.realdata.ParentDashboardRealDataAdapter;
import com.boom.parentdashboard.realdata.ParentDashboardRealMetrics;
import com.boom.student.domain.GradeLevel;
import com.boom.student.domain.Student;
import com.boom.student.domain.TargetSchoolSystem;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class ParentDashboardMockService {

    private final ParentDashboardRealDataAdapter realDataAdapter;

    public ParentDashboardMockService(ParentDashboardRealDataAdapter realDataAdapter) {
        this.realDataAdapter = realDataAdapter;
    }

    public ParentDashboardResponse getDashboard(String requestedLocale, DashboardPeriod period, Student student) {
        Labels labels = labels(requestedLocale);
        CurrentMetrics mockCurrent = currentMetrics(period);
        ParentDashboardRealMetrics realMetrics = realDataAdapter.loadForStudent(student.id(), periodDays(period));

        CurrentMetrics current = realMetrics.hasRealData()
                ? currentMetricsFromReal(realMetrics, mockCurrent.activeGaps())
                : mockCurrent;

        CurrentMetrics previous = previousMetrics(period, current);

        return new ParentDashboardResponse(
                student(student),
                weeklySummary(current),
                selectedPeriod(period, labels),
                metrics(current, previous, labels),
                activityHistory(period),
                realMetrics.hasRealData() ? subjectPerformance(realMetrics) : subjectPerformance(labels),
                learningGaps(labels),
                currentActionPlan(labels),
                recentActivitySummaries(period, labels),
                null
        );
    }

    /**
     * Kept for existing unit tests and local callers.
     * Runtime dashboard requests should use the overload that receives the real Student.
     */
    public ParentDashboardResponse getDashboard(String requestedLocale, DashboardPeriod period) {
        return getDashboard(
                requestedLocale,
                period,
                new Student(
                        UUID.nameUUIDFromBytes("student-helena".getBytes()),
                        "Helena Bevilacqua",
                        null,
                        GradeLevel.GRADE_7,
                        TargetSchoolSystem.ITALY,
                        "pt-BR",
                        com.boom.student.domain.StudentStatus.ACTIVE,
                        java.time.Instant.now(),
                        java.time.Instant.now()
                )
        );
    }

    private ParentDashboardResponse.StudentSummary student(Student student) {
        return new ParentDashboardResponse.StudentSummary(
                student.id().toString(),
                student.displayName(),
                student.gradeLevel().name(),
                student.targetSchoolSystem().name()
        );
    }

    private ParentDashboardResponse.WeeklySummary weeklySummary(CurrentMetrics current) {
        return new ParentDashboardResponse.WeeklySummary(
                current.completedActivities(),
                current.studyTimeMinutes(),
                current.accuracy(),
                3
        );
    }

    private ParentDashboardResponse.SelectedPeriod selectedPeriod(DashboardPeriod period, Labels labels) {
        Locale locale = labels.locale();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d", locale);
        String label = period.startDate().format(formatter) + " - " + period.endDate().format(formatter);
        String comparison = period.comparisonStartDate().format(formatter) + " - " + period.comparisonEndDate().format(formatter);

        return new ParentDashboardResponse.SelectedPeriod(
                period.preset(),
                period.startDate(),
                period.endDate(),
                period.comparisonStartDate(),
                period.comparisonEndDate(),
                label,
                labels.comparedTo() + " " + comparison
        );
    }

    private List<ParentDashboardResponse.MetricSummary> metrics(CurrentMetrics current, CurrentMetrics previous, Labels labels) {
        return List.of(
                metric("completedActivities", labels.completedActivities(), String.valueOf(current.completedActivities()), labels.activitiesHelper(),
                        current.completedActivities(), previous.completedActivities(), true, labels),
                metric("studyTime", labels.studyTime(), formatMinutes(current.studyTimeMinutes()), labels.studyTimeHelper(),
                        current.studyTimeMinutes(), previous.studyTimeMinutes(), true, labels),
                metric("accuracy", labels.accuracy(), current.accuracy() + "%", labels.accuracyHelper(),
                        current.accuracy(), previous.accuracy(), true, labels),
                metric("activeGaps", labels.activeGaps(), String.valueOf(current.activeGaps()), labels.activeGapsHelper(),
                        current.activeGaps(), previous.activeGaps(), false, labels)
        );
    }

    private ParentDashboardResponse.MetricSummary metric(
            String id,
            String label,
            String value,
            String helperText,
            double current,
            double previous,
            boolean higherIsBetter,
            Labels labels
    ) {
        double delta = current - previous;
        double percent = previous == 0 ? 0 : Math.round((delta / previous) * 1000.0) / 10.0;

        TrendDirection direction;
        if (Math.abs(percent) < 1.0) {
            direction = TrendDirection.STABLE;
        } else if ((delta > 0 && higherIsBetter) || (delta < 0 && !higherIsBetter)) {
            direction = TrendDirection.UP;
        } else {
            direction = TrendDirection.DOWN;
        }

        String trendLabel = switch (direction) {
            case UP -> "+" + Math.abs(percent) + "% " + labels.improved();
            case DOWN -> "-" + Math.abs(percent) + "% " + labels.worse();
            case STABLE -> labels.stable();
        };

        return new ParentDashboardResponse.MetricSummary(
                id,
                label,
                value,
                helperText,
                direction,
                trendLabel,
                percent,
                higherIsBetter
        );
    }

    private List<ParentDashboardResponse.ActivityHistoryItem> activityHistory(DashboardPeriod period) {
        LocalDate end = period.endDate();
        return List.of(
                new ParentDashboardResponse.ActivityHistoryItem(end.minusDays(6), 1, 68, 20),
                new ParentDashboardResponse.ActivityHistoryItem(end.minusDays(5), 1, 72, 25),
                new ParentDashboardResponse.ActivityHistoryItem(end.minusDays(4), 0, null, 0),
                new ParentDashboardResponse.ActivityHistoryItem(end.minusDays(3), 1, 76, 30),
                new ParentDashboardResponse.ActivityHistoryItem(end.minusDays(2), 1, 82, 25),
                new ParentDashboardResponse.ActivityHistoryItem(end.minusDays(1), 1, 81, 30),
                new ParentDashboardResponse.ActivityHistoryItem(end, 0, null, 0)
        );
    }

    private CurrentMetrics currentMetricsFromReal(ParentDashboardRealMetrics realMetrics, int fallbackActiveGaps) {
        return new CurrentMetrics(
                realMetrics.completedActivities(),
                secondsToMinutes(realMetrics.totalTimeSpentSeconds()),
                (int) Math.round(realMetrics.accuracy()),
                fallbackActiveGaps
        );
    }

    private int periodDays(DashboardPeriod period) {
        return (int) ChronoUnit.DAYS.between(period.startDate(), period.endDate()) + 1;
    }

    private int secondsToMinutes(int seconds) {
        if (seconds <= 0) {
            return 0;
        }
        return Math.max(1, (int) Math.ceil(seconds / 60.0));
    }

    private List<ParentDashboardResponse.SubjectPerformance> subjectPerformance(ParentDashboardRealMetrics realMetrics) {
        return realMetrics.subjectMetrics().stream()
                .map(subject -> new ParentDashboardResponse.SubjectPerformance(
                        subject.subjectId().toString(),
                        subject.subjectName(),
                        (int) Math.round(subject.accuracy()),
                        secondsToMinutes(subject.totalTimeSpentSeconds()),
                        "REAL_DATA",
                        0
                ))
                .toList();
    }

    private List<ParentDashboardResponse.SubjectPerformance> subjectPerformance(Labels labels) {
        return List.of(
                new ParentDashboardResponse.SubjectPerformance("math", labels.math(), 68, 55, "IMPROVING", 2),
                new ParentDashboardResponse.SubjectPerformance("english", labels.english(), 84, 35, "STABLE", 0),
                new ParentDashboardResponse.SubjectPerformance("science", labels.science(), 73, 40, "IMPROVING", 1)
        );
    }

    private List<ParentDashboardResponse.LearningGap> learningGaps(Labels labels) {
        return List.of(
                new ParentDashboardResponse.LearningGap("gap-fractions", labels.math(), labels.fractions(), labels.equivalentFractions(), "MEDIUM", "IN_PROGRESS", 6, 45),
                new ParentDashboardResponse.LearningGap("gap-science-charts", labels.science(), labels.scientificCharts(), labels.interpretChartEvidence(), "LOW", "OPEN", 2, 18)
        );
    }

    private ParentDashboardResponse.CurrentActionPlan currentActionPlan(Labels labels) {
        return new ParentDashboardResponse.CurrentActionPlan(
                "plan-fractions",
                labels.actionPlanTitle(),
                labels.actionPlanDescription(),
                labels.math(),
                labels.fractions(),
                labels.equivalentFractions(),
                "MEDIUM",
                45,
                40,
                List.of(
                        new ParentDashboardResponse.ActionPlanItem(labels.actionPlanItem1(), 10, "COMPLETED"),
                        new ParentDashboardResponse.ActionPlanItem(labels.actionPlanItem2(), 20, "IN_PROGRESS"),
                        new ParentDashboardResponse.ActionPlanItem(labels.actionPlanItem3(), 15, "PENDING")
                )
        );
    }

    private List<ParentDashboardResponse.RecentActivitySummary> recentActivitySummaries(DashboardPeriod period, Labels labels) {
        return List.of(
                new ParentDashboardResponse.RecentActivitySummary("act-2026-06-28", period.endDate().minusDays(1), labels.fractionsPractice(), labels.math(), 81, 30, labels.fractionsSummary()),
                new ParentDashboardResponse.RecentActivitySummary("act-2026-06-27", period.endDate().minusDays(2), labels.readingComprehension(), labels.english(), 88, 25, labels.readingSummary())
        );
    }

    private CurrentMetrics currentMetrics(DashboardPeriod period) {
        return switch (period.preset()) {
            case LAST_7_DAYS -> new CurrentMetrics(5, 130, 76, 3);
            case LAST_90_DAYS -> new CurrentMetrics(58, 1440, 78, 6);
            case CURRENT_MONTH -> new CurrentMetrics(22, 520, 79, 4);
            case CUSTOM -> new CurrentMetrics(18, 420, 77, 5);
            case LAST_30_DAYS -> new CurrentMetrics(26, 660, 80, 4);
        };
    }

    private CurrentMetrics previousMetrics(DashboardPeriod period, CurrentMetrics current) {
        return switch (period.preset()) {
            case LAST_7_DAYS -> new CurrentMetrics(4, 145, 74, 4);
            case LAST_90_DAYS -> new CurrentMetrics(51, 1380, 76, 7);
            case CURRENT_MONTH -> new CurrentMetrics(19, 560, 79, 4);
            case CUSTOM -> new CurrentMetrics(16, 430, 78, 5);
            case LAST_30_DAYS -> new CurrentMetrics(21, 610, 77, 5);
        };
    }

    private String formatMinutes(int minutes) {
        if (minutes < 60) {
            return minutes + "m";
        }
        int hours = minutes / 60;
        int remaining = minutes % 60;
        return remaining == 0 ? hours + "h" : hours + "h " + remaining + "m";
    }

    private Labels labels(String locale) {
        if (locale != null && locale.toLowerCase().startsWith("pt")) {
            return Labels.pt();
        }
        return Labels.en();
    }

    private record CurrentMetrics(int completedActivities, int studyTimeMinutes, int accuracy, int activeGaps) {
    }

    private record Labels(
            Locale locale,
            String completedActivities,
            String studyTime,
            String accuracy,
            String activeGaps,
            String activitiesHelper,
            String studyTimeHelper,
            String accuracyHelper,
            String activeGapsHelper,
            String comparedTo,
            String improved,
            String worse,
            String stable,
            String math,
            String english,
            String science,
            String fractions,
            String equivalentFractions,
            String scientificCharts,
            String interpretChartEvidence,
            String actionPlanTitle,
            String actionPlanDescription,
            String actionPlanItem1,
            String actionPlanItem2,
            String actionPlanItem3,
            String fractionsPractice,
            String fractionsSummary,
            String readingComprehension,
            String readingSummary
    ) {
        static Labels en() {
            return new Labels(
                    Locale.ENGLISH,
                    "Completed activities",
                    "Study time",
                    "Accuracy",
                    "Active gaps",
                    "Activities completed in the selected period",
                    "Total focused learning time",
                    "Average correct answers",
                    "Open learning gaps",
                    "Compared to",
                    "improved",
                    "needs attention",
                    "stable",
                    "Mathematics",
                    "English",
                    "Science",
                    "Fractions",
                    "Equivalent fractions",
                    "Scientific charts",
                    "Interpret chart evidence",
                    "Review equivalent fractions",
                    "Practice visual comparison of equivalent fractions before moving to proportional reasoning.",
                    "Review visual explanation",
                    "Practice 10 visual fraction questions",
                    "Retry short challenge",
                    "Fractions practice",
                    "Helena improved in visual comparison but still needs practice recognizing equivalent fractions quickly.",
                    "Reading comprehension",
                    "Strong performance identifying the main idea and supporting details in short texts."
            );
        }

        static Labels pt() {
            return new Labels(
                    new Locale("pt", "BR"),
                    "Atividades concluídas",
                    "Tempo de estudo",
                    "Precisão",
                    "Gaps ativos",
                    "Atividades concluídas no período selecionado",
                    "Tempo total de estudo focado",
                    "Média de respostas corretas",
                    "Lacunas de aprendizagem abertas",
                    "Comparado com",
                    "melhorou",
                    "precisa atenção",
                    "estável",
                    "Matemática",
                    "Inglês",
                    "Ciências",
                    "Frações",
                    "Frações equivalentes",
                    "Gráficos científicos",
                    "Interpretar evidências em gráficos",
                    "Revisar frações equivalentes",
                    "Praticar comparação visual de frações equivalentes antes de avançar para raciocínio proporcional.",
                    "Revisar explicação visual",
                    "Praticar 10 questões visuais de frações",
                    "Refazer desafio curto",
                    "Prática de frações",
                    "Helena melhorou na comparação visual, mas ainda precisa praticar o reconhecimento rápido de frações equivalentes.",
                    "Compreensão de leitura",
                    "Bom desempenho identificando ideia principal e detalhes de apoio em textos curtos."
            );
        }
    }
}
