package com.golajugaenyang.gateway.security;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

public class TokenBlacklistValidator implements OAuth2TokenValidator<Jwt> {

    private final TokenBlacklistCache blacklistCache;

    public TokenBlacklistValidator(TokenBlacklistCache blacklistCache) {
        this.blacklistCache = blacklistCache;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        if (blacklistCache.isBlacklisted(token.getTokenValue())) {
            return OAuth2TokenValidatorResult.failure(
                new OAuth2Error("invalid_token", "로그아웃된 토큰입니다.", null)
            );
        }
        return OAuth2TokenValidatorResult.success();
    }
}
