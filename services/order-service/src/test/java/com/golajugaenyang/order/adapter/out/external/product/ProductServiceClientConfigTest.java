package com.golajugaenyang.order.adapter.out.external.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.net.http.HttpClient;
import javax.net.ssl.SSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;

class ProductServiceClientConfigTest {

    private final ProductServiceClientConfig config = new ProductServiceClientConfig();

    @Test
    void appliesInternalMtlsBundleToProductHttpClient() throws Exception {
        SSLContext sslContext = SSLContext.getDefault();
        SslBundle sslBundle = mock(SslBundle.class);
        SslBundles sslBundles = mock(SslBundles.class);
        when(sslBundles.getBundle("internalmtls")).thenReturn(sslBundle);
        when(sslBundle.createSslContext()).thenReturn(sslContext);

        HttpClient client = config.createHttpClient(
            properties("https://generic-service.product-service.svc.cluster.local:8443"),
            true,
            sslBundles);

        assertThat(client.sslContext()).isSameAs(sslContext);
        verify(sslBundles).getBundle("internalmtls");
        verify(sslBundle).createSslContext();
    }

    @Test
    void keepsLocalHttpClientWhenInternalMtlsIsDisabled() {
        SslBundles sslBundles = mock(SslBundles.class);

        HttpClient client = config.createHttpClient(
            properties("http://localhost:8083"), false, sslBundles);

        assertThat(client).isNotNull();
        verifyNoInteractions(sslBundles);
    }

    @Test
    void rejectsHttpUrlWhenInternalMtlsIsEnabled() {
        SslBundles sslBundles = mock(SslBundles.class);

        assertThatThrownBy(() -> config.createHttpClient(
            properties("http://generic-service.product-service.svc.cluster.local:8080"),
            true,
            sslBundles))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("must use HTTPS");
        verifyNoInteractions(sslBundles);
    }

    private ProductServiceProperties properties(String baseUrl) {
        return new ProductServiceProperties(baseUrl, null, null);
    }
}
