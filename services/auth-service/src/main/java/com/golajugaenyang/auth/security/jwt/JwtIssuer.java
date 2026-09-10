package com.golajugaenyang.auth.security.jwt;

import com.nimbusds.jwt.JWTClaimsSet;
import java.time.Instant;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtIssuer {

    private static final String TOKEN_TYPE_CLAIM = "tokenType";
    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";

    private final JwtSigner jwtSigner;

    @Value("${jwt.expiration.access}")
    private long accessTokenExpirationSeconds;

    @Value("${jwt.expiration.refresh}")
    private long refreshTokenExpirationSeconds;

    public String generateAccessToken(Long authId) {
        return generateToken(authId, ACCESS_TOKEN_TYPE, accessTokenExpirationSeconds);
    }

    public String generateRefreshToken(Long authId) {
        return generateToken(authId, REFRESH_TOKEN_TYPE, refreshTokenExpirationSeconds);
    }

    public long getRefreshTokenExpirationSeconds() {
        return refreshTokenExpirationSeconds;
    }

    private String generateToken(Long authId, String tokenType, long expirationSeconds) {
        Instant now = Instant.now();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .subject(String.valueOf(authId))
            .claim(TOKEN_TYPE_CLAIM, tokenType)
            .issueTime(Date.from(now))
            .expirationTime(Date.from(now.plusSeconds(expirationSeconds)))
            .build();

        return jwtSigner.sign(claimsSet);
    }
}
