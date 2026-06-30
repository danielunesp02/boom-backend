package com.boom.auth.api;

import java.util.UUID;

public record PhoneVerificationResponse(UUID guardianId, String status, String message, String devVerificationCode) {
}
