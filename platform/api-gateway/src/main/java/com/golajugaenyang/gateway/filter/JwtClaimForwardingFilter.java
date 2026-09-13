package com.golajugaenyang.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Component
public class JwtClaimForwardingFilter implements GlobalFilter, Ordered {

    private static final String AUTH_ID_HEADER = "X-Auth-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerWebExchange sanitizedExchange = stripClientSuppliedAuthIdHeader(exchange);

        return ReactiveSecurityContextHolder.getContext()
            .map(SecurityContext::getAuthentication)
            .filter(JwtAuthenticationToken.class::isInstance)
            .cast(JwtAuthenticationToken.class)
            .map(JwtAuthenticationToken::getToken)
            .map(jwt -> withAuthIdHeader(sanitizedExchange, jwt))
            .defaultIfEmpty(sanitizedExchange)
            .flatMap(chain::filter);
    }

    private ServerWebExchange stripClientSuppliedAuthIdHeader(ServerWebExchange exchange) {
        ServerHttpRequest strippedRequest = exchange.getRequest().mutate()
            .headers(headers -> headers.remove(AUTH_ID_HEADER))
            .build();

        return exchange.mutate().request(strippedRequest).build();
    }

    private ServerWebExchange withAuthIdHeader(ServerWebExchange exchange, Jwt jwt) {
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
            .header(AUTH_ID_HEADER, jwt.getSubject())
            .build();

        return exchange.mutate().request(mutatedRequest).build();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}
