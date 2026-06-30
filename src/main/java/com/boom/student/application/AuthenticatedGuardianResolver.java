package com.boom.student.application;

import com.boom.auth.security.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
public class AuthenticatedGuardianResolver {

    public UUID requireGuardianId(Authentication authentication) {
        Authentication resolvedAuthentication = authentication;

        if (resolvedAuthentication == null) {
            resolvedAuthentication = SecurityContextHolder.getContext().getAuthentication();
        }

        if (resolvedAuthentication == null || !resolvedAuthentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required.");
        }

        Object principal = resolvedAuthentication.getPrincipal();

        System.out.println("[GUARDIAN_RESOLVER] authenticationClass="
                + resolvedAuthentication.getClass().getName()
                + " principalClass="
                + (principal == null ? "null" : principal.getClass().getName()));

        if (principal instanceof CurrentUser currentUser) {
            UUID guardianAccountId = currentUser.guardianAccountId();

            if (guardianAccountId == null) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Authenticated user is not linked to a guardian account."
                );
            }

            return guardianAccountId;
        }

        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Authenticated principal is not a guardian user."
        );
    }
}