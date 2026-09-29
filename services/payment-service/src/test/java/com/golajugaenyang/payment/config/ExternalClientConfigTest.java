package com.golajugaenyang.payment.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import javax.net.ssl.SSLContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.web.client.RestClient;

class ExternalClientConfigTest {

    private final ExternalClientConfig config = new ExternalClientConfig();
    private HttpServer server;

    @AfterEach
    void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void appliesInternalMtlsBundleToOrderHttpClient() throws Exception {
        SSLContext sslContext = SSLContext.getDefault();
        SslBundle sslBundle = mock(SslBundle.class);
        SslBundles sslBundles = mock(SslBundles.class);
        when(sslBundles.getBundle("internalmtls")).thenReturn(sslBundle);
        when(sslBundle.createSslContext()).thenReturn(sslContext);

        HttpClient client = config.createOrderServiceHttpClient(
            properties("https://generic-service.order-service.svc.cluster.local:8443"),
            true,
            sslBundles);

        assertThat(client.sslContext()).isSameAs(sslContext);
        verify(sslBundles).getBundle("internalmtls");
        verify(sslBundle).createSslContext();
    }

    @Test
    void keepsLocalHttpAndConfiguredTimeoutWhenInternalMtlsIsDisabled() {
        SslBundles sslBundles = mock(SslBundles.class);
        OrderServiceProperties properties = new OrderServiceProperties(
            "http://localhost:8084", Duration.ofSeconds(3), Duration.ofSeconds(4));

        HttpClient client = config.createOrderServiceHttpClient(
            properties, false, sslBundles);

        assertThat(client.connectTimeout()).contains(Duration.ofSeconds(3));
        verifyNoInteractions(sslBundles);
    }

    @Test
    void rejectsHttpUrlWhenInternalMtlsIsEnabled() {
        SslBundles sslBundles = mock(SslBundles.class);

        assertThatThrownBy(() -> config.createOrderServiceHttpClient(
            properties("http://generic-service.order-service.svc.cluster.local:8080"),
            true,
            sslBundles))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("must use HTTPS");
        verifyNoInteractions(sslBundles);
    }

    @Test
    void keepsInternalGatewaySecretHeaderOnOrderCalls() throws Exception {
        AtomicReference<String> internalSecret = new AtomicReference<>();
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/internal/probe", exchange -> {
            internalSecret.set(exchange.getRequestHeaders().getFirst("X-Internal-Secret"));
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
        });
        server.start();
        String baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
        SslBundles sslBundles = mock(SslBundles.class);

        RestClient client = config.orderServiceRestClient(
            properties(baseUrl), "expected-internal-secret", false, sslBundles);
        client.get().uri("/internal/probe").retrieve().toBodilessEntity();

        assertThat(internalSecret.get()).isEqualTo("expected-internal-secret");
        verifyNoInteractions(sslBundles);
    }

    @Test
    void tossClientRemainsIndependentFromInternalMtls() {
        TossPaymentProperties properties = new TossPaymentProperties(
            "https://api.tosspayments.com", "test-secret", null, null);

        RestClient tossClient = config.tossPaymentRestClient(properties);

        assertThat(tossClient).isNotNull();
    }

    private OrderServiceProperties properties(String baseUrl) {
        return new OrderServiceProperties(baseUrl, null, null);
    }
}
