package com.golajugaenyang.common.security.config;

import com.golajugaenyang.common.security.filter.InternalSecretFeignInterceptor;
import feign.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;

public class InternalFeignClientConfig {

    @Bean
    public InternalSecretFeignInterceptor internalSecretFeignInterceptor(
        @Value("${internal.gateway-secret}") String internalGatewaySecret
    ) {
        return new InternalSecretFeignInterceptor(internalGatewaySecret);
    }

    // 서비스 간 mTLS(internal.mtls.enabled=true)일 때만 등록됨 — 아직 켜지 않은
    // 서비스에서는 이 Bean 자체가 안 만들어져서 "internal-mtls"라는 SSL 번들을 찾다가
    // 실패하는 일이 없다(내부 mTLS는 order-service부터 순차 적용 중, 2026-09-17).
    //
    // Feign 기본 클라이언트(feign.Client.Default)는 생성자로 SSLSocketFactory를 받는
    // 방식으로 TLS를 구성한다 — 이 SSLSocketFactory가 "내 인증서를 상대에게 제시"
    // (client auth)와 "상대 인증서가 우리 CA가 발급한 게 맞는지 검증"(trust)을 둘 다
    // 담당한다. 이 서비스가 서버로서 쓰는 것과 같은 internal-mtls 번들
    // (application.yml의 spring.ssl.bundle.pem.internal-mtls)을 그대로 재사용한다.
    @Bean
    @ConditionalOnProperty(name = "internal.mtls.enabled", havingValue = "true")
    public Client internalMtlsFeignClient(SslBundles sslBundles) {
        var sslBundle = sslBundles.getBundle("internal-mtls");
        return new Client.Default(sslBundle.createSslContext().getSocketFactory(), null);
    }
}
