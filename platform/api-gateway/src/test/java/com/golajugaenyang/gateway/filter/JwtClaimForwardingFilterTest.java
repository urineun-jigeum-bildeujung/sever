package com.golajugaenyang.gateway.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

class JwtClaimForwardingFilterTest {

    private static final String FAKE_INTERNAL_SECRET = "test-only-internal-secret";

    private final JwtClaimForwardingFilter filter =
        new JwtClaimForwardingFilter(FAKE_INTERNAL_SECRET);

    @Test
    void removesSpoofedHeadersAndAddsOnlyVerifiedJwtClaims() {
        MockServerWebExchange exchange = exchangeWithSpoofedHeaders();
        AtomicReference<ServerWebExchange> forwarded = new AtomicReference<>();
        GatewayFilterChain chain = captured -> {
            forwarded.set(captured);
            return Mono.empty();
        };

        Jwt jwt = Jwt.withTokenValue("signed-test-token")
            .header("alg", "RS256")
            .subject("auth-123")
            .claim("memberId", "member-456")
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(60))
            .build();

        filter.filter(exchange, chain)
            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(
                new JwtAuthenticationToken(jwt)))
            .block();

        HttpHeaders headers = forwarded.get().getRequest().getHeaders();
        assertEquals("auth-123", headers.getFirst("X-Auth-Id"));
        assertEquals("member-456", headers.getFirst("X-Member-Id"));
        assertEquals(FAKE_INTERNAL_SECRET, headers.getFirst("X-Internal-Secret"));
    }

    @Test
    void removesAllIdentityHeadersFromUnauthenticatedRequest() {
        MockServerWebExchange exchange = exchangeWithSpoofedHeaders();
        AtomicReference<ServerWebExchange> forwarded = new AtomicReference<>();
        GatewayFilterChain chain = captured -> {
            forwarded.set(captured);
            return Mono.empty();
        };

        filter.filter(exchange, chain).block();

        HttpHeaders headers = forwarded.get().getRequest().getHeaders();
        assertNull(headers.getFirst("X-Auth-Id"));
        assertNull(headers.getFirst("X-Member-Id"));
        assertNull(headers.getFirst("X-Internal-Secret"));
    }

    @Test
    void doesNotInventMemberIdWhenClaimIsAbsent() {
        AtomicReference<ServerWebExchange> forwarded = new AtomicReference<>();
        Jwt jwt = Jwt.withTokenValue("signed-test-token")
            .header("alg", "RS256")
            .subject("auth-123")
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(60))
            .build();

        filter.filter(exchangeWithSpoofedHeaders(), captured -> {
            forwarded.set(captured);
            return Mono.empty();
        }).contextWrite(ReactiveSecurityContextHolder.withAuthentication(
            new JwtAuthenticationToken(jwt))).block();

        assertNull(forwarded.get().getRequest().getHeaders().getFirst("X-Member-Id"));
    }

    private MockServerWebExchange exchangeWithSpoofedHeaders() {
        return MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/products")
            .header("X-Auth-Id", "spoofed-auth")
            .header("X-Member-Id", "spoofed-member")
            .header("X-Internal-Secret", "spoofed-secret")
            .build());
    }
}
