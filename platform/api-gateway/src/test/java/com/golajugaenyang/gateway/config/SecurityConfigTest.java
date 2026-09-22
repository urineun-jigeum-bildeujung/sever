package com.golajugaenyang.gateway.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.webclient.autoconfigure.WebClientSsl;
import org.springframework.web.reactive.function.client.WebClient;

import com.golajugaenyang.gateway.security.TokenBlacklistCache;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    private SecurityConfig securityConfig;
    private TokenBlacklistCache blacklistCache;

    @Mock
    private WebClientSsl webClientSsl;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig();
        blacklistCache = new TokenBlacklistCache();
    }

    @Test
    void appliesInternalMtlsBundleToJwksClientInInfra() {
        Consumer<WebClient.Builder> noOpSsl = builder -> { };
        when(webClientSsl.fromBundle("internalmtls")).thenReturn(noOpSsl);

        var decoder = securityConfig.jwtDecoder(
            "https://auth.example.test/oauth2/jwks", true, blacklistCache,
            WebClient.builder(), webClientSsl);

        assertNotNull(decoder);
        verify(webClientSsl).fromBundle("internalmtls");
    }

    @Test
    void keepsLocalJwksClientWithoutInternalMtlsBundle() {
        var decoder = securityConfig.jwtDecoder(
            "http://localhost:8081/oauth2/jwks", false, blacklistCache,
            WebClient.builder(), webClientSsl);

        assertNotNull(decoder);
        verify(webClientSsl, never()).fromBundle(eq("internalmtls"));
    }
}
