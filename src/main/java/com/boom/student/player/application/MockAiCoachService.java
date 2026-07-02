package com.boom.student.player.application;

import com.boom.learning.domain.ActivityQuestion;
import com.boom.learning.domain.LearningActivity;
import com.boom.student.player.api.dto.StudentPlayerAiMessageResponse;
import org.springframework.stereotype.Service;

@Service
public class MockAiCoachService implements AiCoachService {

    @Override
    public StudentPlayerAiMessageResponse activityIntro(LearningActivity activity, String studentName) {
        String message = "Olá, " + studentName + "! Vamos praticar " + activity.title()
                + ". Leia com calma, toque na resposta que parece melhor e eu te ajudo no caminho.";
        return new StudentPlayerAiMessageResponse("ENCOURAGING", message);
    }

    @Override
    public StudentPlayerAiMessageResponse questionHint(LearningActivity activity, ActivityQuestion question) {
        String message = switch (question.complexityLevel()) {
            case RECALL -> "Dica: procure lembrar a ideia principal antes de escolher.";
            case UNDERSTAND -> "Dica: compare o significado das opções, não apenas os números ou palavras.";
            case APPLY -> "Dica: tente aplicar a regra em um exemplo simples antes de responder.";
            case ANALYZE -> "Dica: divida o problema em partes menores.";
            case EVALUATE -> "Dica: elimine as opções menos prováveis e compare as restantes.";
            case CREATE -> "Dica: pense em como você explicaria isso para outra pessoa.";
        };

        return new StudentPlayerAiMessageResponse("HELPFUL", message);
    }

    @Override
    public StudentPlayerAiMessageResponse completionPreview(LearningActivity activity, String studentName) {
        String message = "Quando terminar, eu vou te mostrar seus acertos, pontos de atenção e o próximo passo recomendado.";
        return new StudentPlayerAiMessageResponse("SUPPORTIVE", message);
    }
}
