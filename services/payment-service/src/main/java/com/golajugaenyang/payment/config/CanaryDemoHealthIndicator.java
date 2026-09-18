package com.golajugaenyang.payment.config;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

// 카나리 롤백 시연용 임시 코드(2026-09-18, canary-bluegreen-rollback-scenario.md 실행
// 단계). DB 접속을 끊는 방식은 Hibernate 부트스트랩이 커넥션을 요구해서 앱 자체가
// 크래시하고, crash-loop는 HTTP 레벨 5xx로 안 잡혀 AnalysisTemplate이 감지 못 함을
// 실제로 확인함. 이 HealthIndicator는 DB와 무관하게 /actuator/health를 무조건
// DOWN(503)으로 응답시켜 AnalysisTemplate의 5xx 에러율 감지를 확실하게 유도한다.
// 시연 끝나면 이 파일 삭제할 것.
@Component
public class CanaryDemoHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        return Health.down().withDetail("reason", "canary rollback demo - intentional failure").build();
    }
}
