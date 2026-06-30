package com.boom.auth.security;

import java.util.UUID;

public record CurrentUser(
        UUID subjectId,
        UUID guardianAccountId,
        String subjectType,
        String displayName,
        String username,
        String email
) {
}
