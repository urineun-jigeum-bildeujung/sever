package com.golajugaenyang.common.test.postgres;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class PostgresIntegrationTestSupport {
    private static final String DEFAULT_IMAGE = "postgres:16";

    // Jenkins Pod엔 Docker 데몬이 없어 Testcontainers가 Postgres 컨테이너를 못 띄운다
    // (DockerClientProviderStrategy 실패, 2026-10-02 sever-ci dev #4/#5 실제 확인).
    // CI_POSTGRES_URL이 있으면 Jenkinsfile이 같은 Pod에 고정으로 올려둔 Postgres
    // 사이드카에 바로 접속하고, 없으면(로컬 개발 환경) 기존처럼 Testcontainers가 직접 띄운다.
    private static final String CI_POSTGRES_URL = System.getenv("CI_POSTGRES_URL");

    protected static final PostgreSQLContainer<?> POSTGRES;

    static {
        if (CI_POSTGRES_URL == null) {
            String image = System.getProperty("postgres.testcontainers.image", DEFAULT_IMAGE);
            POSTGRES = new PostgreSQLContainer<>(DockerImageName.parse(image));
            POSTGRES.start();
        } else {
            POSTGRES = null;
        }
    }

    @DynamicPropertySource
    static void registerPostgresProperties(DynamicPropertyRegistry registry) {
        if (CI_POSTGRES_URL != null) {
            registry.add("spring.datasource.url", () -> CI_POSTGRES_URL);
            registry.add("spring.datasource.username", () -> System.getenv("CI_POSTGRES_USER"));
            registry.add("spring.datasource.password", () -> System.getenv("CI_POSTGRES_PASSWORD"));
        } else {
            registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
            registry.add("spring.datasource.username", POSTGRES::getUsername);
            registry.add("spring.datasource.password", POSTGRES::getPassword);
            registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        }
    }
}
