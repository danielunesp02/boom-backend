CREATE TABLE IF NOT EXISTS guardian_account (
    id UUID PRIMARY KEY,
    display_name VARCHAR(120) NOT NULL,
    username VARCHAR(80),
    email VARCHAR(160),
    phone_number_hash VARCHAR(128) NOT NULL,
    phone_number_masked VARCHAR(32) NOT NULL,
    phone_verified_at TIMESTAMPTZ,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    last_login_at TIMESTAMPTZ
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_guardian_account_username
ON guardian_account (LOWER(username))
WHERE username IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uq_guardian_account_email
ON guardian_account (LOWER(email))
WHERE email IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uq_guardian_account_phone_hash
ON guardian_account (phone_number_hash);

CREATE TABLE IF NOT EXISTS password_credential (
    id UUID PRIMARY KEY,
    guardian_account_id UUID NOT NULL REFERENCES guardian_account(id) ON DELETE CASCADE,
    password_hash TEXT NOT NULL,
    algorithm VARCHAR(64) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    last_changed_at TIMESTAMPTZ,
    status VARCHAR(32) NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_password_credential_guardian_active
ON password_credential (guardian_account_id)
WHERE status = 'ACTIVE';

CREATE TABLE IF NOT EXISTS identity_document (
    id UUID PRIMARY KEY,
    guardian_account_id UUID NOT NULL REFERENCES guardian_account(id) ON DELETE CASCADE,
    country VARCHAR(2) NOT NULL,
    document_type VARCHAR(64) NOT NULL,
    document_number_hash VARCHAR(128) NOT NULL,
    document_number_masked VARCHAR(64) NOT NULL,
    verified_at TIMESTAMPTZ,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_identity_document_hash
ON identity_document (country, document_type, document_number_hash);

CREATE TABLE IF NOT EXISTS phone_verification_challenge (
    id UUID PRIMARY KEY,
    guardian_account_id UUID NOT NULL REFERENCES guardian_account(id) ON DELETE CASCADE,
    phone_number_hash VARCHAR(128) NOT NULL,
    challenge_code_hash VARCHAR(128) NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    attempts INTEGER NOT NULL DEFAULT 0,
    max_attempts INTEGER NOT NULL DEFAULT 5,
    verified_at TIMESTAMPTZ,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_phone_verification_guardian_status
ON phone_verification_challenge (guardian_account_id, status, expires_at);

CREATE TABLE IF NOT EXISTS auth_session (
    id UUID PRIMARY KEY,
    subject_type VARCHAR(32) NOT NULL,
    subject_id UUID NOT NULL,
    guardian_account_id UUID,
    student_profile_id UUID,
    session_token_hash VARCHAR(128) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked_at TIMESTAMPTZ,
    last_used_at TIMESTAMPTZ,
    status VARCHAR(32) NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_auth_session_token_hash
ON auth_session (session_token_hash);
