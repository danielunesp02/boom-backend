package com.boom.auth.service;

import com.boom.auth.api.*;
import com.boom.auth.config.AuthProperties;
import com.boom.auth.security.CurrentUser;
import com.boom.auth.security.HashingService;
import com.boom.auth.security.InMemoryRateLimiter;
import com.boom.auth.security.MaskingService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final HashingService hashingService;
    private final MaskingService maskingService;
    private final InMemoryRateLimiter rateLimiter;
    private final AuthProperties authProperties;

    public AuthService(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder, HashingService hashingService,
                       MaskingService maskingService, InMemoryRateLimiter rateLimiter, AuthProperties authProperties) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
        this.hashingService = hashingService;
        this.maskingService = maskingService;
        this.rateLimiter = rateLimiter;
        this.authProperties = authProperties;
    }

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        String rateKey = "signup:" + hashingService.stableHash(request.username() + ":" + request.phoneNumber());
        if (!rateLimiter.allow(rateKey, 5, 60)) {
            throw new AuthException("RATE_LIMITED", "Too many attempts. Please try again later.");
        }

        UUID guardianId = UUID.randomUUID();
        Instant now = Instant.now();
        String phoneHash = hashingService.stableHash(request.phoneNumber());
        String documentHash = hashingService.stableHash(request.country() + ":" + request.documentType() + ":" + request.documentNumber());

        if (exists("SELECT COUNT(*) FROM guardian_account WHERE phone_number_hash = ?", phoneHash)
                || exists("SELECT COUNT(*) FROM identity_document WHERE country = ? AND document_type = ? AND document_number_hash = ?",
                request.country().toUpperCase(), request.documentType().toUpperCase(), documentHash)) {
            throw new AuthException("SIGNUP_NOT_AVAILABLE", "Unable to create account with the submitted information.");
        }

        jdbcTemplate.update(
                "INSERT INTO guardian_account (id, display_name, username, email, phone_number_hash, phone_number_masked, status, created_at, updated_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, 'PENDING_PHONE_VERIFICATION', ?, ?)",
                guardianId, request.displayName(), normalizeNullable(request.username()), normalizeNullable(request.email()),
                phoneHash, maskingService.maskPhone(request.phoneNumber()), Timestamp.from(now), Timestamp.from(now));

        jdbcTemplate.update(
                "INSERT INTO password_credential (id, guardian_account_id, password_hash, algorithm, created_at, updated_at, last_changed_at, status) " +
                        "VALUES (?, ?, ?, 'BCRYPT', ?, ?, ?, 'ACTIVE')",
                UUID.randomUUID(), guardianId, passwordEncoder.encode(request.password()),
                Timestamp.from(now), Timestamp.from(now), Timestamp.from(now));

        jdbcTemplate.update(
                "INSERT INTO identity_document (id, guardian_account_id, country, document_type, document_number_hash, document_number_masked, status, created_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, 'SUBMITTED', ?)",
                UUID.randomUUID(), guardianId, request.country().toUpperCase(), request.documentType().toUpperCase(),
                documentHash, maskingService.maskDocument(request.documentNumber()), Timestamp.from(now));

        String devCode = createPhoneVerificationChallenge(guardianId, phoneHash, now);

        return new SignupResponse(guardianId, "PENDING_PHONE_VERIFICATION", maskingService.maskPhone(request.phoneNumber()),
                "Account created. Please confirm the phone verification code.", authProperties.devMode() ? devCode : null);
    }

    @Transactional
    public PhoneVerificationResponse startPhoneVerification(UUID guardianId) {
        if (!rateLimiter.allow("phone-start:" + guardianId, 3, 60)) {
            throw new AuthException("RATE_LIMITED", "Too many attempts. Please try again later.");
        }
        GuardianRecord guardian = findGuardianById(guardianId)
                .orElseThrow(() -> new AuthException("NOT_FOUND", "Unable to process verification."));
        String devCode = createPhoneVerificationChallenge(guardianId, guardian.phoneNumberHash(), Instant.now());
        return new PhoneVerificationResponse(guardianId, "PENDING", "Verification code generated.",
                authProperties.devMode() ? devCode : null);
    }

    @Transactional
    public PhoneVerificationResponse confirmPhoneVerification(UUID guardianId, String code) {
        if (!rateLimiter.allow("phone-confirm:" + guardianId, 5, 60)) {
            throw new AuthException("RATE_LIMITED", "Too many attempts. Please try again later.");
        }

        Instant now = Instant.now();
        int updated = jdbcTemplate.update(
                "UPDATE phone_verification_challenge SET status = 'VERIFIED', verified_at = ? " +
                        "WHERE guardian_account_id = ? AND challenge_code_hash = ? AND status = 'PENDING' AND expires_at > ? AND attempts < max_attempts",
                Timestamp.from(now), guardianId, hashingService.stableHash(code), Timestamp.from(now));

        jdbcTemplate.update("UPDATE phone_verification_challenge SET attempts = attempts + 1 WHERE guardian_account_id = ? AND status = 'PENDING'",
                guardianId);

        if (updated == 0) {
            throw new AuthException("INVALID_VERIFICATION_CODE", "Invalid or expired verification code.");
        }

        jdbcTemplate.update("UPDATE guardian_account SET status = 'ACTIVE', phone_verified_at = ?, updated_at = ? WHERE id = ?",
                Timestamp.from(now), Timestamp.from(now), guardianId);

        return new PhoneVerificationResponse(guardianId, "ACTIVE", "Phone verified successfully.", null);
    }

    @Transactional
    public LoginResult login(LoginRequest request) {
        String identifier = normalizeNullable(request.identifier());
        if (!rateLimiter.allow("login:" + hashingService.stableHash(identifier), 8, 60)) {
            throw new BadCredentialsException("Invalid credentials");
        }

        GuardianRecord guardian = findGuardianByIdentifier(identifier)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        String passwordHash = findPasswordHash(guardian.id())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), passwordHash)) {
            throw new BadCredentialsException("Invalid credentials");
        }

        if (!"ACTIVE".equals(guardian.status())) {
            throw new AuthException("ACCOUNT_NOT_ACTIVE", "Please complete account verification before logging in.");
        }

        String sessionToken = hashingService.randomToken();
        String sessionHash = hashingService.tokenHash(sessionToken);
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(authProperties.sessionTtlMinutes() * 60L);

        jdbcTemplate.update(
                "INSERT INTO auth_session (id, subject_type, subject_id, guardian_account_id, session_token_hash, created_at, expires_at, last_used_at, status) " +
                        "VALUES (?, 'GUARDIAN', ?, ?, ?, ?, ?, ?, 'ACTIVE')",
                UUID.randomUUID(), guardian.id(), guardian.id(), sessionHash,
                Timestamp.from(now), Timestamp.from(expiresAt), Timestamp.from(now));

        jdbcTemplate.update("UPDATE guardian_account SET last_login_at = ?, updated_at = ? WHERE id = ?",
                Timestamp.from(now), Timestamp.from(now), guardian.id());

        return new LoginResult(sessionToken, toResponse(guardian));
    }

    @Transactional
    public void logout(String sessionToken) {
        jdbcTemplate.update("UPDATE auth_session SET status = 'REVOKED', revoked_at = ? WHERE session_token_hash = ? AND status = 'ACTIVE'",
                Timestamp.from(Instant.now()), hashingService.tokenHash(sessionToken));
    }

    @Transactional
    public Optional<CurrentUser> validateSessionToken(String sessionToken) {
        String sessionHash = hashingService.tokenHash(sessionToken);
        Instant now = Instant.now();

        Optional<CurrentUser> user = jdbcTemplate.query(
                "SELECT ga.id, ga.display_name, ga.username, ga.email " +
                        "FROM auth_session s JOIN guardian_account ga ON ga.id = s.guardian_account_id " +
                        "WHERE s.session_token_hash = ? AND s.status = 'ACTIVE' AND s.expires_at > ? AND ga.status = 'ACTIVE'",
                rs -> {
                    if (!rs.next()) return Optional.empty();
                    UUID guardianId = rs.getObject("id", UUID.class);
                    return Optional.of(new CurrentUser(guardianId, guardianId, "GUARDIAN",
                            rs.getString("display_name"), rs.getString("username"), rs.getString("email")));
                },
                sessionHash, Timestamp.from(now));

        user.ifPresent(ignored -> jdbcTemplate.update("UPDATE auth_session SET last_used_at = ? WHERE session_token_hash = ?",
                Timestamp.from(now), sessionHash));

        return user;
    }

    public AuthUserResponse me(UUID guardianId) {
        GuardianRecord guardian = findGuardianById(guardianId)
                .orElseThrow(() -> new AuthException("NOT_FOUND", "User not found."));
        return toResponse(guardian);
    }

    private String createPhoneVerificationChallenge(UUID guardianId, String phoneHash, Instant now) {
        jdbcTemplate.update("UPDATE phone_verification_challenge SET status = 'EXPIRED' WHERE guardian_account_id = ? AND status = 'PENDING'",
                guardianId);

        String code = hashingService.randomNumericCode(6);
        jdbcTemplate.update(
                "INSERT INTO phone_verification_challenge (id, guardian_account_id, phone_number_hash, challenge_code_hash, expires_at, attempts, max_attempts, status, created_at) " +
                        "VALUES (?, ?, ?, ?, ?, 0, 5, 'PENDING', ?)",
                UUID.randomUUID(), guardianId, phoneHash, hashingService.stableHash(code),
                Timestamp.from(now.plusSeconds(600)), Timestamp.from(now));
        return code;
    }

    private Optional<GuardianRecord> findGuardianByIdentifier(String identifier) {
        return jdbcTemplate.query(
                "SELECT id, display_name, username, email, phone_number_hash, phone_number_masked, status FROM guardian_account " +
                        "WHERE LOWER(username) = LOWER(?) OR LOWER(email) = LOWER(?)",
                rs -> rs.next() ? Optional.of(mapGuardian(rs)) : Optional.empty(), identifier, identifier);
    }

    private Optional<GuardianRecord> findGuardianById(UUID guardianId) {
        return jdbcTemplate.query(
                "SELECT id, display_name, username, email, phone_number_hash, phone_number_masked, status FROM guardian_account WHERE id = ?",
                rs -> rs.next() ? Optional.of(mapGuardian(rs)) : Optional.empty(), guardianId);
    }

    private Optional<String> findPasswordHash(UUID guardianId) {
        return jdbcTemplate.query(
                "SELECT password_hash FROM password_credential WHERE guardian_account_id = ? AND status = 'ACTIVE'",
                rs -> rs.next() ? Optional.of(rs.getString("password_hash")) : Optional.empty(), guardianId);
    }

    private boolean exists(String sql, Object... args) {
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, args);
        return count != null && count > 0;
    }

    private GuardianRecord mapGuardian(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new GuardianRecord(rs.getObject("id", UUID.class), rs.getString("display_name"),
                rs.getString("username"), rs.getString("email"), rs.getString("phone_number_hash"),
                rs.getString("phone_number_masked"), rs.getString("status"));
    }

    private AuthUserResponse toResponse(GuardianRecord guardian) {
        return new AuthUserResponse(guardian.id(), guardian.displayName(), guardian.username(),
                guardian.email(), guardian.status());
    }

    private String normalizeNullable(String value) {
        return value == null ? null : value.trim().toLowerCase();
    }

    public record LoginResult(String sessionToken, AuthUserResponse user) {
    }

    private record GuardianRecord(UUID id, String displayName, String username, String email,
                                  String phoneNumberHash, String phoneNumberMasked, String status) {
    }
}
