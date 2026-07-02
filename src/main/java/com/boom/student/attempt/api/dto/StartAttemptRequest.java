package com.boom.student.attempt.api.dto;

public record StartAttemptRequest(
        String sourceChannel,
        String locale
) {
}
