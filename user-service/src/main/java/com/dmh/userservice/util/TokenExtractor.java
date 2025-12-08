package com.dmh.userservice.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class TokenExtractor {

    public String getEmailFromToken(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalArgumentException("Authentication or principal is null");
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        String email = jwt.getClaim("email");

        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email claim not found in JWT token");
        }

        return email;
    }

    public String getUserIdFromToken(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalArgumentException("Authentication or principal is null");
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        return jwt.getSubject();
    }

    public String getClaim(Authentication authentication, String claimName) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalArgumentException("Authentication or principal is null");
        }

        if (claimName == null || claimName.isEmpty()) {
            throw new IllegalArgumentException("Claim name cannot be null or empty");
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        Object claim = jwt.getClaim(claimName);

        return claim != null ? claim.toString() : null;
    }
}