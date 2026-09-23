package com.golajugaenyang.auth.security.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.golajugaenyang.auth.adapter.out.client.MemberClient;
import com.golajugaenyang.auth.adapter.out.client.dto.MemberIdResponse;
import com.golajugaenyang.auth.application.AuthService;
import com.golajugaenyang.auth.application.recods.TokenPair;
import com.golajugaenyang.auth.domain.repository.AuthRepository;
import com.nimbusds.jwt.JWTClaimsSet;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtSecurityCompatibilityTest {

    private static final long ACCESS_TOKEN_EXPIRATION_SECONDS = 300;
    private static final long REFRESH_TOKEN_EXPIRATION_SECONDS = 3600;

    private JwtKeyProvider keyProvider;
    private JwtSigner signer;
    private JwtVerifier verifier;
    private JwtIssuer issuer;

    @BeforeEach
    void setUp() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();

        keyProvider = new JwtKeyProvider(toPkcs8Pem(keyPair));
        signer = new JwtSigner(keyProvider);
        verifier = new JwtVerifier(keyProvider);
        issuer = new JwtIssuer(signer);
        ReflectionTestUtils.setField(
            issuer,
            "accessTokenExpirationSeconds",
            ACCESS_TOKEN_EXPIRATION_SECONDS
        );
        ReflectionTestUtils.setField(
            issuer,
            "refreshTokenExpirationSeconds",
            REFRESH_TOKEN_EXPIRATION_SECONDS
        );
    }

    @Test
    void loadsPkcs8KeyAndPublishesPublicJwk() {
        assertFalse(keyProvider.getKeyId().isBlank());
        assertEquals(1, keyProvider.getPublicJWKSet().getKeys().size());
        assertFalse(keyProvider.getPublicJWKSet().getKeys().getFirst().isPrivate());
    }

    @Test
    void signsAndVerifiesAccessAndRefreshTokens() throws Exception {
        Long authId = 101L;
        Long memberId = 202L;

        String accessToken = issuer.generateAccessToken(authId, memberId);
        String refreshToken = issuer.generateRefreshToken(authId, memberId);

        JWTClaimsSet accessClaims = verifier.verifyAccessToken(accessToken).orElseThrow();
        JWTClaimsSet refreshClaims = verifier.verifyRefreshToken(refreshToken).orElseThrow();

        assertEquals(authId.toString(), accessClaims.getSubject());
        assertEquals(memberId, accessClaims.getLongClaim("memberId"));
        assertEquals("access", accessClaims.getStringClaim("tokenType"));
        assertEquals("refresh", refreshClaims.getStringClaim("tokenType"));
        assertTrue(verifier.verifyRefreshToken(accessToken).isEmpty());
        assertTrue(verifier.verifyAccessToken(refreshToken).isEmpty());
        assertTrue(verifier.verifyAccessToken(tamperSignature(accessToken)).isEmpty());
    }

    @Test
    void rejectsExpiredToken() {
        JWTClaimsSet expiredClaims = new JWTClaimsSet.Builder()
            .subject("101")
            .claim("tokenType", "access")
            .issueTime(Date.from(Instant.now().minusSeconds(120)))
            .expirationTime(Date.from(Instant.now().minusSeconds(60)))
            .build();

        assertTrue(verifier.verifyAccessToken(signer.sign(expiredClaims)).isEmpty());
    }

    @Test
    void refreshesTokensWithMockedStateStores() {
        Long authId = 101L;
        Long memberId = 202L;
        String oldRefreshToken = issuer.generateRefreshToken(authId, memberId);

        AuthRepository authRepository = mock(AuthRepository.class);
        RefreshTokenStore refreshTokenStore = mock(RefreshTokenStore.class);
        LoginCodeStore loginCodeStore = mock(LoginCodeStore.class);
        MemberClient memberClient = mock(MemberClient.class);
        TokenBlacklistPublisher blacklistPublisher = mock(TokenBlacklistPublisher.class);

        when(memberClient.getMemberId(authId)).thenReturn(new MemberIdResponse(memberId));
        when(refreshTokenStore.rotate(
            eq(authId),
            eq(oldRefreshToken),
            anyString(),
            eq(REFRESH_TOKEN_EXPIRATION_SECONDS)
        )).thenReturn(true);

        AuthService authService = new AuthService(
            authRepository,
            issuer,
            refreshTokenStore,
            loginCodeStore,
            memberClient,
            verifier,
            blacklistPublisher
        );

        TokenPair refreshed = authService.refreshTokens(oldRefreshToken);

        assertEquals(memberId, refreshed.memberId());
        assertTrue(verifier.verifyAccessToken(refreshed.accessToken()).isPresent());
        assertTrue(verifier.verifyRefreshToken(refreshed.refreshToken()).isPresent());
        verify(refreshTokenStore).rotate(
            eq(authId),
            eq(oldRefreshToken),
            eq(refreshed.refreshToken()),
            anyLong()
        );
    }

    private static String toPkcs8Pem(KeyPair keyPair) {
        String base64 = Base64.getMimeEncoder(64, "\n".getBytes(StandardCharsets.US_ASCII))
            .encodeToString(keyPair.getPrivate().getEncoded());
        return "-----BEGIN PRIVATE KEY-----\n" + base64 + "\n-----END PRIVATE KEY-----";
    }

    private static String tamperSignature(String token) {
        String[] parts = token.split("\\.");
        char replacement = parts[2].charAt(10) == 'A' ? 'B' : 'A';
        parts[2] = parts[2].substring(0, 10) + replacement + parts[2].substring(11);
        return String.join(".", parts);
    }
}
