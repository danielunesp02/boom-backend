package com.boom.auth.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank String displayName,
        @NotBlank String username,
        @Email String email,
        @NotBlank String phoneNumber,
        @NotBlank String country,
        @NotBlank String documentType,
        @NotBlank String documentNumber,
        @NotBlank @Size(min = 10, max = 200) String password
) {
}
