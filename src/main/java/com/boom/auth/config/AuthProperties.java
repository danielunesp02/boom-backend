package com.boom.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "boom.auth")
public record AuthProperties(
        String cookieName,
        int sessionTtlMinutes,
        boolean devMode,
        String hashPepper
) {
    public AuthProperties {
        if (cookieName == null || cookieName.isBlank()) {
            cookieName = "BOOM_SESSION";
        }
        if (sessionTtlMinutes <= 0) {
            sessionTtlMinutes = 720;
        }
        if (hashPepper == null || hashPepper.isBlank()) {
            hashPepper = "local-dev-change-me";
        }
    }
}
