package com.boom.auth.security;

import com.boom.auth.config.AuthProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashingServiceTest {

    private final HashingService hashingService = new HashingService(
            new AuthProperties("BOOM_SESSION", 720, true, "test-pepper")
    );

    @Test
    void shouldGenerateStableHashForSameValue() {
        String first = hashingService.stableHash("Daniel@example.com");
        String second = hashingService.stableHash(" daniel@example.com ");

        assertEquals(first, second);
        assertNotEquals("daniel@example.com", first);
    }

    @Test
    void shouldGenerateRandomTokens() {
        String first = hashingService.randomToken();
        String second = hashingService.randomToken();

        assertNotEquals(first, second);
        assertTrue(first.length() >= 32);
    }
}
