package com.golajugaenyang.auth.security.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Date;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtVerifier {

    private static final String TOKEN_TYPE_CLAIM = "tokenType";
    private static final String REFRESH_TOKEN_TYPE = "refresh";
    private static final String ACCESS_TOKEN_TYPE = "access";

    private final JwtKeyProvider jwtKeyProvider;

    public Optional<JWTClaimsSet> verifyRefreshToken(String token) {
        return verify(token, REFRESH_TOKEN_TYPE);
    }

    public Optional<JWTClaimsSet> verifyAccessToken(String token) {
        return verify(token, ACCESS_TOKEN_TYPE);
    }

    private Optional<JWTClaimsSet> verify(String token, String expectedTokenType) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            if (!signedJWT.verify(new RSASSAVerifier((RSAPublicKey) jwtKeyProvider.getPublicKey()))) {
                return Optional.empty();
            }

            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            if (claimsSet.getExpirationTime() == null || claimsSet.getExpirationTime().before(new Date())) {
                return Optional.empty();
            }

            if (!expectedTokenType.equals(claimsSet.getClaim(TOKEN_TYPE_CLAIM))) {
                return Optional.empty();
            }

            return Optional.of(claimsSet);

        } catch (ParseException | JOSEException e) {
            return Optional.empty();
        }
    }
}
