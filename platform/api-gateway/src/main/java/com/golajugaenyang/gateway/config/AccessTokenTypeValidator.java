package com.golajugaenyang.gateway.config;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

public class AccessTokenTypeValidator implements OAuth2TokenValidator<Jwt> {

    private static final String TOKEN_TYPE_CLAIM = "tokenType";
    private static final String ACCESS_TOKEN_TYPE = "access";

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        if (ACCESS_TOKEN_TYPE.equals(token.getClaimAsString(TOKEN_TYPE_CLAIM))) {
            return OAuth2TokenValidatorResult.success();
        }
        return OAuth2TokenValidatorResult.failure(
            new OAuth2Error("invalid_token", "유효하지 않은 토큰입니다.", null)
        );
    }
}
