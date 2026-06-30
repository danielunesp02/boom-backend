package com.boom.auth.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record PhoneVerificationConfirmRequest(@NotNull UUID guardianId, @NotBlank String code) {
}
