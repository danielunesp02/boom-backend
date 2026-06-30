package com.boom.auth.security;

import com.boom.auth.config.AuthProperties;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;

@Service
public class HashingService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private final AuthProperties authProperties;

    public HashingService(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    public String stableHash(String value) {
        return sha256(authProperties.hashPepper() + ":" + normalize(value));
    }

    public String tokenHash(String token) {
        return sha256("session:" + authProperties.hashPepper() + ":" + token);
    }

    public String randomNumericCode(int digits) {
        int min = (int) Math.pow(10, digits - 1);
        int max = (int) Math.pow(10, digits) - 1;
        return String.valueOf(SECURE_RANDOM.nextInt(max - min + 1) + min);
    }

    public String randomToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(input.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to hash value", exception);
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
