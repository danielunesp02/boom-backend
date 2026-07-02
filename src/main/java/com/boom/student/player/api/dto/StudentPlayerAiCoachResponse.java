package com.boom.student.player.api.dto;

public record StudentPlayerAiCoachResponse(
        String coachName,
        String avatarKey,
        StudentPlayerAiMessageResponse introMessage,
        StudentPlayerAiMessageResponse completionPreview
) {
}
