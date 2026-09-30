package com.golajugaenyang.auth.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * Spring Boot의 spring.session.store-type=redis 자동설정이 이 Boot 버전에서 걸리지 않아 명시적으로 활성화
 * OAuth2 로그인의 인가요청(state)을파드 간 공유되는 Redis 세션에 저장해서, 로그인 시작/콜백이 서로 다른 파드로 가도 찾을 수 있게 함.
 */
@Configuration
@EnableRedisHttpSession
public class SessionConfig {
}
