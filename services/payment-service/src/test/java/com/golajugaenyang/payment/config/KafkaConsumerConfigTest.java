package com.golajugaenyang.payment.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.test.util.ReflectionTestUtils;

class KafkaConsumerConfigTest {

    @Test
    void enablesKafkaListenerInfrastructure() {
        assertThat(KafkaConsumerConfig.class).hasAnnotation(EnableKafka.class);
    }

    @Test
    void consumerFactoryPropagatesSaslSslProperties() {
        KafkaConsumerConfig config = configWith(
            "SASL_SSL",
            "SCRAM-SHA-512",
            "test-jaas-config",
            "/etc/kafka-tls/ca.crt",
            "PEM"
        );

        DefaultKafkaConsumerFactory<?, ?> factory =
            (DefaultKafkaConsumerFactory<?, ?>) config.consumerFactory();
        Map<String, Object> properties = factory.getConfigurationProperties();

        assertThat(properties)
            .containsEntry(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL")
            .containsEntry(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-512")
            .containsEntry(SaslConfigs.SASL_JAAS_CONFIG, "test-jaas-config")
            .containsEntry(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "/etc/kafka-tls/ca.crt")
            .containsEntry(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, "PEM");
    }

    @Test
    void consumerFactoryRejectsIncompleteSaslConfiguration() {
        KafkaConsumerConfig config = configWith(
            "SASL_SSL",
            "SCRAM-SHA-512",
            "",
            "/etc/kafka-tls/ca.crt",
            "PEM"
        );

        assertThatThrownBy(config::consumerFactory)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("sasl.mechanism/sasl.jaas.config");
    }

    private static KafkaConsumerConfig configWith(
        String securityProtocol,
        String saslMechanism,
        String saslJaasConfig,
        String trustStoreLocation,
        String trustStoreType
    ) {
        KafkaConsumerConfig config = new KafkaConsumerConfig();
        ReflectionTestUtils.setField(config, "bootstrapServers", "kafka:9093");
        ReflectionTestUtils.setField(config, "groupId", "payment-service.refund-consumer");
        ReflectionTestUtils.setField(config, "securityProtocol", securityProtocol);
        ReflectionTestUtils.setField(config, "saslMechanism", saslMechanism);
        ReflectionTestUtils.setField(config, "saslJaasConfig", saslJaasConfig);
        ReflectionTestUtils.setField(config, "sslTrustStoreLocation", trustStoreLocation);
        ReflectionTestUtils.setField(config, "sslTrustStoreType", trustStoreType);
        return config;
    }
}
