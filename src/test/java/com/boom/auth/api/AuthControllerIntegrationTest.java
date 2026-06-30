package com.boom.auth.api;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Template for the real MockMvc auth flow.
 *
 * Enable this after confirming the project has:
 * - spring-security-test
 * - test database configuration
 * - migrations enabled for tests
 *
 * Recommended assertions:
 * 1. POST /api/v1/auth/signup returns guardianId and devVerificationCode.
 * 2. password_credential.password_hash does not equal raw password.
 * 3. identity_document.document_number_masked does not expose full document.
 * 4. POST /api/v1/auth/phone-verification/confirm activates account.
 * 5. POST /api/v1/auth/login returns Set-Cookie BOOM_SESSION.
 * 6. GET /api/v1/auth/me with cookie returns guardian.
 * 7. POST /api/v1/auth/logout invalidates session.
 * 8. GET /api/v1/auth/me after logout returns 401/403.
 */
@Disabled("Enable after test database wiring is confirmed")
class AuthControllerIntegrationTest {

    @Test
    void shouldSignupVerifyLoginMeAndLogout() {
        // Implement with MockMvc once the project's test database strategy is finalized.
    }
}
