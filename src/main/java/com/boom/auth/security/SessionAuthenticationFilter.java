package com.boom.auth.security;

import com.boom.auth.config.AuthProperties;
import com.boom.auth.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class SessionAuthenticationFilter extends OncePerRequestFilter {

    private final AuthProperties authProperties;
    private final AuthService authService;

    public SessionAuthenticationFilter(AuthProperties authProperties, AuthService authService) {
        this.authProperties = authProperties;
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        Optional<String> sessionToken = readCookie(request);

        System.out.println("[SESSION_FILTER] path=" + request.getRequestURI()
                + " hasCookie=" + sessionToken.isPresent());

        if (sessionToken.isEmpty() || sessionToken.get().isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<CurrentUser> currentUser = authService.validateSessionToken(sessionToken.get());

        System.out.println("[SESSION_FILTER] currentUserPresent=" + currentUser.isPresent());

        if (currentUser.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        currentUser.get(),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_GUARDIAN"))
                );

        SecurityContextHolder.clearContext();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        System.out.println("[SESSION_FILTER] authenticated="
                + SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                + " authorities="
                + SecurityContextHolder.getContext().getAuthentication().getAuthorities());

        filterChain.doFilter(request, response);
    }

    private Optional<String> readCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return Optional.empty();
        }

        for (Cookie cookie : cookies) {
            if (authProperties.cookieName().equals(cookie.getName())) {
                return Optional.ofNullable(cookie.getValue());
            }
        }

        return Optional.empty();
    }
}