package com.boom.auth.api;

import java.util.UUID;

public record SignupResponse(UUID guardianId, String status, String phoneNumberMasked, String message, String devVerificationCode) {
}
