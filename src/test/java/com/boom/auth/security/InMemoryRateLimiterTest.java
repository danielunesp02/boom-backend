package com.boom.auth.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryRateLimiterTest {

    @Test
    void shouldBlockWhenLimitIsExceeded() {
        InMemoryRateLimiter limiter = new InMemoryRateLimiter();

        assertTrue(limiter.allow("login:test", 2, 60));
        assertTrue(limiter.allow("login:test", 2, 60));
        assertFalse(limiter.allow("login:test", 2, 60));
    }
}
