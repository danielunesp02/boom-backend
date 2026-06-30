package com.boom.parentdashboard.application;

import com.boom.parentdashboard.api.dto.ParentDashboardResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParentDashboardMockService {

    public ParentDashboardResponse getDashboard(String boomLocale, String acceptLanguage) {
        DashboardLocale locale = DashboardLocale.resolve(boomLocale, acceptLanguage);

        return new ParentDashboardResponse(
                student(locale),
                weeklySummary(),
                activityHistory(),
                subjectPerformance(locale),
                learningGaps(locale),
                currentActionPlan(locale),
                recentActivitySummaries(locale),
                null
        );
    }

    private ParentDashboardResponse.Student student(DashboardLocale locale) {
        return switch (locale) {
            case PT_BR -> new ParentDashboardResponse.Student(
                    "student-helena",
                    "Helena",
                    "Ensino fundamental II",
                    "Itália - Scuola Secondaria di Primo Grado"
            );
            case IT_IT -> new ParentDashboardResponse.Student(
                    "student-helena",
                    "Helena",
                    "Scuola secondaria inferiore",
                    "Italia - Scuola Secondaria di Primo Grado"
            );
            case ES_ES -> new ParentDashboardResponse.Student(
                    "student-helena",
                    "Helena",
                    "Secundaria inferior",
                    "Italia - Scuola Secondaria di Primo Grado"
            );
            case EN_US -> new ParentDashboardResponse.Student(
                    "student-helena",
                    "Helena",
                    "Lower Secondary",
                    "Italy - Scuola Secondaria di Primo Grado"
            );
        };
    }

    private ParentDashboardResponse.WeeklySummary weeklySummary() {
        return new ParentDashboardResponse.WeeklySummary(5, 130, 76, 3);
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

    private List<ParentDashboardResponse.SubjectPerformance> subjectPerformance(DashboardLocale locale) {
        return switch (locale) {
            case PT_BR -> List.of(
                    new ParentDashboardResponse.SubjectPerformance("math", "Matemática", 68, 55, "IMPROVING", 2),
                    new ParentDashboardResponse.SubjectPerformance("english", "Inglês", 84, 35, "STABLE", 0),
                    new ParentDashboardResponse.SubjectPerformance("science", "Ciências", 73, 40, "IMPROVING", 1)
            );
            case IT_IT -> List.of(
                    new ParentDashboardResponse.SubjectPerformance("math", "Matematica", 68, 55, "IMPROVING", 2),
                    new ParentDashboardResponse.SubjectPerformance("english", "Inglese", 84, 35, "STABLE", 0),
                    new ParentDashboardResponse.SubjectPerformance("science", "Scienze", 73, 40, "IMPROVING", 1)
            );
            case ES_ES -> List.of(
                    new ParentDashboardResponse.SubjectPerformance("math", "Matemáticas", 68, 55, "IMPROVING", 2),
                    new ParentDashboardResponse.SubjectPerformance("english", "Inglés", 84, 35, "STABLE", 0),
                    new ParentDashboardResponse.SubjectPerformance("science", "Ciencias", 73, 40, "IMPROVING", 1)
            );
            case EN_US -> List.of(
                    new ParentDashboardResponse.SubjectPerformance("math", "Mathematics", 68, 55, "IMPROVING", 2),
                    new ParentDashboardResponse.SubjectPerformance("english", "English", 84, 35, "STABLE", 0),
                    new ParentDashboardResponse.SubjectPerformance("science", "Science", 73, 40, "IMPROVING", 1)
            );
        };
    }

    private List<ParentDashboardResponse.LearningGap> learningGaps(DashboardLocale locale) {
        return switch (locale) {
            case PT_BR -> List.of(
                    new ParentDashboardResponse.LearningGap(
                            "gap-fractions",
                            "Matemática",
                            "Frações",
                            "Frações equivalentes",
                            "MEDIUM",
                            "IN_PROGRESS",
                            6,
                            45
                    ),
                    new ParentDashboardResponse.LearningGap(
                            "gap-science-charts",
                            "Ciências",
                            "Gráficos científicos",
                            "Interpretar evidências em gráficos",
                            "LOW",
                            "OPEN",
                            2,
                            18
                    )
            );
            case IT_IT -> List.of(
                    new ParentDashboardResponse.LearningGap(
                            "gap-fractions",
                            "Matematica",
                            "Frazioni",
                            "Frazioni equivalenti",
                            "MEDIUM",
                            "IN_PROGRESS",
                            6,
                            45
                    ),
                    new ParentDashboardResponse.LearningGap(
                            "gap-science-charts",
                            "Scienze",
                            "Grafici scientifici",
                            "Interpretare evidenze nei grafici",
                            "LOW",
                            "OPEN",
                            2,
                            18
                    )
            );
            case ES_ES -> List.of(
                    new ParentDashboardResponse.LearningGap(
                            "gap-fractions",
                            "Matemáticas",
                            "Fracciones",
                            "Fracciones equivalentes",
                            "MEDIUM",
                            "IN_PROGRESS",
                            6,
                            45
                    ),
                    new ParentDashboardResponse.LearningGap(
                            "gap-science-charts",
                            "Ciencias",
                            "Gráficos científicos",
                            "Interpretar evidencia en gráficos",
                            "LOW",
                            "OPEN",
                            2,
                            18
                    )
            );
            case EN_US -> List.of(
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
        };
    }

    private ParentDashboardResponse.CurrentActionPlan currentActionPlan(DashboardLocale locale) {
        return switch (locale) {
            case PT_BR -> new ParentDashboardResponse.CurrentActionPlan(
                    "plan-fractions",
                    "Revisar frações equivalentes",
                    "Praticar comparação visual de frações equivalentes antes de avançar para raciocínio proporcional.",
                    "Matemática",
                    "Frações",
                    "Frações equivalentes",
                    "MEDIUM",
                    45,
                    40,
                    List.of(
                            new ParentDashboardResponse.ActionPlanItem("Revisar explicação visual", 10, "COMPLETED"),
                            new ParentDashboardResponse.ActionPlanItem("Praticar 10 questões visuais de frações", 20, "IN_PROGRESS"),
                            new ParentDashboardResponse.ActionPlanItem("Refazer desafio curto", 15, "PENDING")
                    )
            );
            case IT_IT -> new ParentDashboardResponse.CurrentActionPlan(
                    "plan-fractions",
                    "Ripassare le frazioni equivalenti",
                    "Esercitare il confronto visivo delle frazioni equivalenti prima di passare al ragionamento proporzionale.",
                    "Matematica",
                    "Frazioni",
                    "Frazioni equivalenti",
                    "MEDIUM",
                    45,
                    40,
                    List.of(
                            new ParentDashboardResponse.ActionPlanItem("Rivedere la spiegazione visiva", 10, "COMPLETED"),
                            new ParentDashboardResponse.ActionPlanItem("Esercitare 10 domande visive sulle frazioni", 20, "IN_PROGRESS"),
                            new ParentDashboardResponse.ActionPlanItem("Ripetere una sfida breve", 15, "PENDING")
                    )
            );
            case ES_ES -> new ParentDashboardResponse.CurrentActionPlan(
                    "plan-fractions",
                    "Repasar fracciones equivalentes",
                    "Practicar la comparación visual de fracciones equivalentes antes de avanzar al razonamiento proporcional.",
                    "Matemáticas",
                    "Fracciones",
                    "Fracciones equivalentes",
                    "MEDIUM",
                    45,
                    40,
                    List.of(
                            new ParentDashboardResponse.ActionPlanItem("Revisar explicación visual", 10, "COMPLETED"),
                            new ParentDashboardResponse.ActionPlanItem("Practicar 10 preguntas visuales de fracciones", 20, "IN_PROGRESS"),
                            new ParentDashboardResponse.ActionPlanItem("Reintentar desafío corto", 15, "PENDING")
                    )
            );
            case EN_US -> new ParentDashboardResponse.CurrentActionPlan(
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
        };
    }

    private List<ParentDashboardResponse.RecentActivitySummary> recentActivitySummaries(DashboardLocale locale) {
        return switch (locale) {
            case PT_BR -> List.of(
                    new ParentDashboardResponse.RecentActivitySummary(
                            "act-2026-06-28",
                            "2026-06-28",
                            "Prática de frações",
                            "Matemática",
                            81,
                            30,
                            "Helena melhorou na comparação visual, mas ainda precisa praticar o reconhecimento rápido de frações equivalentes."
                    ),
                    new ParentDashboardResponse.RecentActivitySummary(
                            "act-2026-06-27",
                            "2026-06-27",
                            "Compreensão de leitura",
                            "Inglês",
                            88,
                            25,
                            "Bom desempenho ao identificar a ideia principal e detalhes de apoio em textos curtos."
                    )
            );
            case IT_IT -> List.of(
                    new ParentDashboardResponse.RecentActivitySummary(
                            "act-2026-06-28",
                            "2026-06-28",
                            "Esercizio sulle frazioni",
                            "Matematica",
                            81,
                            30,
                            "Helena è migliorata nel confronto visivo, ma deve ancora esercitarsi a riconoscere rapidamente le frazioni equivalenti."
                    ),
                    new ParentDashboardResponse.RecentActivitySummary(
                            "act-2026-06-27",
                            "2026-06-27",
                            "Comprensione del testo",
                            "Inglese",
                            88,
                            25,
                            "Buon risultato nell'identificare l'idea principale e i dettagli di supporto in testi brevi."
                    )
            );
            case ES_ES -> List.of(
                    new ParentDashboardResponse.RecentActivitySummary(
                            "act-2026-06-28",
                            "2026-06-28",
                            "Práctica de fracciones",
                            "Matemáticas",
                            81,
                            30,
                            "Helena mejoró en la comparación visual, pero aún necesita practicar el reconocimiento rápido de fracciones equivalentes."
                    ),
                    new ParentDashboardResponse.RecentActivitySummary(
                            "act-2026-06-27",
                            "2026-06-27",
                            "Comprensión lectora",
                            "Inglés",
                            88,
                            25,
                            "Buen desempeño al identificar la idea principal y los detalles de apoyo en textos cortos."
                    )
            );
            case EN_US -> List.of(
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
        };
    }
}
