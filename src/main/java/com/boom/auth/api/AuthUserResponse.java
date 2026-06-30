package com.boom.auth.api;

import java.util.UUID;

public record AuthUserResponse(UUID guardianId, String displayName, String username, String email, String status) {
}
