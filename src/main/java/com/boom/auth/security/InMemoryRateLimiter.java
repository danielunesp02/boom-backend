package com.boom.auth.security;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryRateLimiter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public boolean allow(String key, int maxAttempts, long windowSeconds) {
        Instant now = Instant.now();
        Bucket bucket = buckets.compute(key, (ignored, current) -> {
            if (current == null || current.windowStart.plusSeconds(windowSeconds).isBefore(now)) {
                return new Bucket(now, 1);
            }
            return new Bucket(current.windowStart, current.count + 1);
        });
        return bucket.count <= maxAttempts;
    }

    private record Bucket(Instant windowStart, int count) {
    }
}
