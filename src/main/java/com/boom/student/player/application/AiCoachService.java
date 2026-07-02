package com.boom.student.player.application;

import com.boom.learning.domain.ActivityQuestion;
import com.boom.learning.domain.LearningActivity;
import com.boom.student.player.api.dto.StudentPlayerAiMessageResponse;

public interface AiCoachService {

    StudentPlayerAiMessageResponse activityIntro(LearningActivity activity, String studentName);

    StudentPlayerAiMessageResponse questionHint(LearningActivity activity, ActivityQuestion question);

    StudentPlayerAiMessageResponse completionPreview(LearningActivity activity, String studentName);
}
