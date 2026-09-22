package com.golajugaenyang.gateway.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.golajugaenyang.gateway.config.AccessTokenTypeValidator;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;

class TokenValidationTest {

    @Test
    void acceptsOnlyAccessTokenType() {
        AccessTokenTypeValidator validator = new AccessTokenTypeValidator();

        assertFalse(validator.validate(jwt("access", Instant.now().plusSeconds(60))).hasErrors());
        assertTrue(validator.validate(jwt("refresh", Instant.now().plusSeconds(60))).hasErrors());
        assertTrue(validator.validate(jwt(null, Instant.now().plusSeconds(60))).hasErrors());
    }

    @Test
    void rejectsExpiredToken() {
        Jwt expired = jwt("access", Instant.now().minusSeconds(60));

        assertTrue(JwtValidators.createDefault().validate(expired).hasErrors());
    }

    @Test
    void rejectsBlacklistedTokenAndAllowsOtherToken() {
        TokenBlacklistCache cache = new TokenBlacklistCache();
        TokenBlacklistValidator validator = new TokenBlacklistValidator(cache);
        Jwt blacklisted = jwt("access", "blacklisted-token", Instant.now().plusSeconds(60));
        Jwt allowed = jwt("access", "allowed-token", Instant.now().plusSeconds(60));

        cache.blacklist(blacklisted.getTokenValue(), 60);

        assertTrue(validator.validate(blacklisted).hasErrors());
        assertFalse(validator.validate(allowed).hasErrors());
    }

    @Test
    void ignoresNonPositiveBlacklistTtl() {
        TokenBlacklistCache cache = new TokenBlacklistCache();
        Jwt token = jwt("access", Instant.now().plusSeconds(60));

        cache.blacklist(token.getTokenValue(), 0);

        assertFalse(cache.isBlacklisted(token.getTokenValue()));
    }

    private Jwt jwt(String tokenType, Instant expiresAt) {
        return jwt(tokenType, "test-token", expiresAt);
    }

    private Jwt jwt(String tokenType, String tokenValue, Instant expiresAt) {
        Jwt.Builder builder = Jwt.withTokenValue(tokenValue)
            .header("alg", "RS256")
            .subject("auth-123")
            .issuedAt(expiresAt.minusSeconds(60))
            .expiresAt(expiresAt);
        if (tokenType != null) {
            builder.claim("tokenType", tokenType);
        }
        return builder.build();
    }
}
