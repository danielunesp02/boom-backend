package com.boom.student.player.api.dto;

public record StudentPlayerOptionResponse(
        String optionId,
        String label,
        String text,
        int displayOrder
) {
}
