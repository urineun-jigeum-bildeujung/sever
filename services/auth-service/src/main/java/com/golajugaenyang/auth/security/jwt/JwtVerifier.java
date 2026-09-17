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

    private final JwtKeyProvider jwtKeyProvider;

    public Optional<JWTClaimsSet> verifyRefreshToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            if (!signedJWT.verify(new RSASSAVerifier((RSAPublicKey) jwtKeyProvider.getPublicKey()))) {
                return Optional.empty();
            }

            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            if (claimsSet.getExpirationTime() == null || claimsSet.getExpirationTime().before(new Date())) {
                return Optional.empty();
            }

            if (!REFRESH_TOKEN_TYPE.equals(claimsSet.getClaim(TOKEN_TYPE_CLAIM))) {
                return Optional.empty();
            }

            return Optional.of(claimsSet);

        } catch (ParseException | JOSEException e) {
            return Optional.empty();
        }
    }
}
