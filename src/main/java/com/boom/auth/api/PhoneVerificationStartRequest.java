package com.boom.auth.api;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record PhoneVerificationStartRequest(@NotNull UUID guardianId) {
}
