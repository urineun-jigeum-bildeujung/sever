package com.golajugaenyang.common.test.postgres;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class PostgresIntegrationTestSupport {
    private static final String DEFAULT_IMAGE = "postgres:16";

    protected static final PostgreSQLContainer<?> POSTGRES;

    static {
        String image = System.getProperty("postgres.testcontainers.image", DEFAULT_IMAGE);
        POSTGRES = new PostgreSQLContainer<>(DockerImageName.parse(image));
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void registerPostgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }
}
