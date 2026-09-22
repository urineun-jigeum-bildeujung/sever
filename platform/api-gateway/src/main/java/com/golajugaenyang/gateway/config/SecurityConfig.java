package com.golajugaenyang.gateway.config;

import com.golajugaenyang.gateway.security.TokenBlacklistCache;
import com.golajugaenyang.gateway.security.TokenBlacklistValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.webclient.autoconfigure.WebClientSsl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    @Order(1)
    public SecurityWebFilterChain publicFilterChain(ServerHttpSecurity http) {
        return http
            .securityMatcher(ServerWebExchangeMatchers.pathMatchers(
                "/api/v1/auths/token/exchange", "/api/v1/auths/token/refresh"))
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchange -> exchange.anyExchange().permitAll())
            .build();
    }

    @Bean
    @Order(2)
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ReactiveJwtDecoder jwtDecoder) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchange -> exchange.anyExchange().permitAll())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtDecoder(jwtDecoder)))
            .build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder(
        @Value("${jwt.jwks-uri}") String jwksUri,
        @Value("${internal.mtls.enabled:false}") boolean internalMtlsEnabled,
        TokenBlacklistCache blacklistCache,
        WebClient.Builder webClientBuilder,
        WebClientSsl webClientSsl
    ) {
        var decoderBuilder = NimbusReactiveJwtDecoder.withJwkSetUri(jwksUri);
        if (internalMtlsEnabled) {
            WebClient jwksWebClient = webClientBuilder.clone()
                .apply(webClientSsl.fromBundle("internalmtls"))
                .build();
            decoderBuilder.webClient(jwksWebClient);
        }

        NimbusReactiveJwtDecoder decoder = decoderBuilder.build();

        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(
            JwtValidators.createDefault(),
            new AccessTokenTypeValidator(),
            new TokenBlacklistValidator(blacklistCache)
        );
        decoder.setJwtValidator(validator);

        return decoder;
    }
}
