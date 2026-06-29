package com.boom.platform.system.api.dto;

import java.time.Instant;

public record PingResponse(
        String message,
        Instant timestamp
) {
}
