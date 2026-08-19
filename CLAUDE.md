# golajugaenyang-server — 루트 컨텍스트

이 문서는 Claude Code가 이 모노레포에서 작업할 때 항상 참고해야 하는 전역 규칙입니다.
서비스별 세부 규칙은 각 `services/*/CLAUDE.md`를 함께 참고하세요.

## 아키텍처 개요
- Java 25 / Spring Boot 4.1.0 / Gradle(Groovy DSL) / YAML
- 모노레포 + MSA 멀티모듈, 서비스 간 메서드 직접 호출 금지
- DDD 기반, 서비스별로 헥사고날 또는 4-Layer 혼용 가능

## 모듈 경계 규칙 (반드시 지킬 것)
- `modules/common-core`: 공통 응답 포맷, 예외 처리, 유틸, CORS/인터셉터. 도메인 로직 절대 금지.
- `modules/common-event`: Kafka 이벤트 payload/enum만. 서비스 내부 도메인 절대 금지.
- `modules/common-security`: 인증/인가 공통 로직. 필요한 서비스만 선택적으로 의존.
- `modules/common-test`: Testcontainers 등. testImplementation으로만 의존.
- `platform/*`: 도메인 로직 없는 실행 서비스 (api-gateway 등). `-service` 접미사 사용하지 않음.
- `services/*`: 도메인 바운디드 컨텍스트. `-service` 접미사 사용.

## 작업 시 주의사항
- 공통 모듈(`modules/`) 변경은 여러 서비스에 영향을 주므로, 별도 턴/PR로 분리해서 작업할 것.
- 특정 서비스 작업을 요청받으면 해당 서비스 디렉토리 밖의 코드는 수정하지 말 것.

## 실행/검증
- 인프라만 기동: `docker compose -f local-infra/docker-compose.yml up -d`
- 특정 서비스 실행: `./gradlew :services:{service-name}:bootRun --args='--spring.profiles.active=local'`
- 공통 모듈 변경 후에는 `./gradlew build`로 전체 영향도를 확인할 것.
