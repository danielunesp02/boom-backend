package com.boom.student.attempt.application;

import com.boom.student.attempt.api.dto.AiCoachFeedbackResponse;
import org.springframework.stereotype.Service;

@Service
public class MockAnswerFeedbackService {

    public AiCoachFeedbackResponse feedback(boolean correct, String explanation) {
        if (correct) {
            String message = explanation == null || explanation.isBlank()
                    ? "Muito bem! Você escolheu a melhor resposta."
                    : "Muito bem! " + explanation;
            return new AiCoachFeedbackResponse("CORRECT", message);
        }

        String message = explanation == null || explanation.isBlank()
                ? "Boa tentativa. Vamos revisar a ideia e tentar aprender com essa resposta."
                : "Boa tentativa. Olhe esta explicação: " + explanation;

        return new AiCoachFeedbackResponse("SUPPORTIVE_REVIEW", message);
    }
}
