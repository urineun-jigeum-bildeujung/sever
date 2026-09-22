package com.golajugaenyang.gateway.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

class ApplicationInfraConfigTest {

    private PropertySource<?> properties;

    @BeforeEach
    void loadInfraConfiguration() throws IOException {
        List<PropertySource<?>> loaded = new YamlPropertySourceLoader().load(
            "application-infra",
            new ClassPathResource("application-infra.yml")
        );
        properties = loaded.getFirst();
    }

    @Test
    void keepsLocalRoutePathsAndRequiresEnvironmentUris() {
        String[][] expectedRoutes = {
            {"auth-service", "${AUTH_SERVICE_URL}", "Path=/api/v1/auths/**"},
            {"auth-service-oauth2", "${AUTH_SERVICE_URL}",
                "Path=/api/auth/oauth2/authorization/**,/api/auth/login/oauth2/code/**"},
            {"member-service", "${MEMBER_SERVICE_URL}", "Path=/api/v1/members/**"},
            {"member-service-pets", "${MEMBER_SERVICE_URL}", "Path=/api/v1/pets/**"},
            {"product-service", "${PRODUCT_SERVICE_URL}",
                "Path=/api/v1/products/**,/api/v1/time-deals/**"},
            {"order-service", "${ORDER_SERVICE_URL}", "Path=/api/v1/orders/**,/api/v1/carts/**"},
            {"payment-service", "${PAYMENT_SERVICE_URL}", "Path=/api/v1/payments/**"},
            {"review-service", "${REVIEW_SERVICE_URL}", "Path=/api/v1/reviews/**"},
            {"notification-service", "${NOTIFICATION_SERVICE_URL}", "Path=/api/v1/notifications/**"}
        };

        for (int index = 0; index < expectedRoutes.length; index++) {
            String prefix = "spring.cloud.gateway.server.webflux.routes[" + index + "]";
            assertEquals(expectedRoutes[index][0], property(prefix + ".id"));
            assertEquals(expectedRoutes[index][1], property(prefix + ".uri"));
            assertEquals(expectedRoutes[index][2], property(prefix + ".predicates[0]"));
        }
    }

    @Test
    void configuresMtlsSeparatelyForGatewayJwksAndRedisClients() {
        assertEquals("internalmtls",
            property("spring.cloud.gateway.server.webflux.httpclient.ssl.ssl-bundle"));
        assertEquals(true, property("spring.data.redis.ssl.enabled"));
        assertEquals("internalmtls", property("spring.data.redis.ssl.bundle"));
        assertEquals(true, property("internal.mtls.enabled"));
        assertEquals("file:/etc/mtls/tls.crt",
            property("spring.ssl.bundle.pem.internalmtls.keystore.certificate"));
        assertEquals("file:/etc/mtls/tls.key",
            property("spring.ssl.bundle.pem.internalmtls.keystore.private-key"));
        assertEquals("file:/etc/mtls/ca.crt",
            property("spring.ssl.bundle.pem.internalmtls.truststore.certificate"));
    }

    @Test
    void hasNoInfraLocalhostFallbacks() {
        assertEquals("${JWT_JWKS_URI}", property("jwt.jwks-uri"));
        assertEquals("${INTERNAL_GATEWAY_SECRET}", property("internal.gateway-secret"));
        assertEquals("${REDIS_HOST}", property("spring.data.redis.host"));
        assertEquals("${REDIS_PORT}", property("spring.data.redis.port"));
    }

    private Object property(String name) {
        return properties.getProperty(name);
    }
}
