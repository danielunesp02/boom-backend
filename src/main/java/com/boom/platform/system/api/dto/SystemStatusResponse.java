package com.boom.platform.system.api.dto;

import java.time.Instant;

public record SystemStatusResponse(
        String status,
        String service,
        Instant timestamp
) {
}
