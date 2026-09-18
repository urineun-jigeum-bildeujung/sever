package com.golajugaenyang.product.config;


import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    // SASL 전환 5단계(2026-09-18). 값이 없으면(현재 평문 9092) 기존 동작 그대로 유지 -
    // gitops-value 6단계에서 이 값들이 채워지기 전까지는 조건문이 항상 false라 안전함.
    @Value("${spring.kafka.security.protocol:}")
    private String securityProtocol;

    @Value("${spring.kafka.properties.sasl.mechanism:}")
    private String saslMechanism;

    @Value("${spring.kafka.properties.sasl.jaas.config:}")
    private String saslJaasConfig;

    @Value("${spring.kafka.ssl.trust-store-location:}")
    private String sslTrustStoreLocation;

    @Value("${spring.kafka.ssl.trust-store-type:}")
    private String sslTrustStoreType;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        if (!securityProtocol.isBlank()) {
            configProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
            if (securityProtocol.startsWith("SASL")) {
                if (saslMechanism.isBlank() || saslJaasConfig.isBlank()) {
                    throw new IllegalStateException(
                        "spring.kafka.security.protocol=" + securityProtocol
                            + " 이지만 sasl.mechanism/sasl.jaas.config가 비어있습니다.");
                }
                configProps.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
                configProps.put(SaslConfigs.SASL_JAAS_CONFIG, saslJaasConfig);
            }
            if (!sslTrustStoreLocation.isBlank()) {
                configProps.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, sslTrustStoreLocation);
            }
            if (!sslTrustStoreType.isBlank()) {
                configProps.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, sslTrustStoreType);
            }
        }
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(
        ProducerFactory<String, String> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }
}
