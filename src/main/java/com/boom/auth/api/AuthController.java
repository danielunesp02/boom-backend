package com.boom.auth.api;

import com.boom.auth.config.AuthProperties;
import com.boom.auth.security.CurrentUser;
import com.boom.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthProperties authProperties;

    public AuthController(AuthService authService, AuthProperties authProperties) {
        this.authService = authService;
        this.authProperties = authProperties;
    }

    @PostMapping("/signup")
    public SignupResponse signup(@Valid @RequestBody SignupRequest request) {
        return authService.signup(request);
    }

    @PostMapping("/phone-verification/start")
    public PhoneVerificationResponse startPhoneVerification(@Valid @RequestBody PhoneVerificationStartRequest request) {
        return authService.startPhoneVerification(request.guardianId());
    }

    @PostMapping("/phone-verification/confirm")
    public PhoneVerificationResponse confirmPhoneVerification(@Valid @RequestBody PhoneVerificationConfirmRequest request) {
        return authService.confirmPhoneVerification(request.guardianId(), request.code());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthUserResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        AuthService.LoginResult result = authService.login(request);
        response.addHeader("Set-Cookie", sessionCookie(result.sessionToken(), authProperties.sessionTtlMinutes()).toString());
        return ResponseEntity.ok(result.user());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(value = "BOOM_SESSION", required = false) String sessionToken,
                                       HttpServletResponse response) {
        if (sessionToken != null && !sessionToken.isBlank()) {
            authService.logout(sessionToken);
        }
        response.addHeader("Set-Cookie", sessionCookie("", 0).toString());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public AuthUserResponse me(Authentication authentication) {
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        return authService.me(currentUser.guardianAccountId());
    }

    private ResponseCookie sessionCookie(String value, int maxAgeMinutes) {
        return ResponseCookie.from(authProperties.cookieName(), value)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofMinutes(maxAgeMinutes))
                .build();
    }
}
