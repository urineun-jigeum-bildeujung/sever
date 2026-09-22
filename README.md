# golajugaenyang-server

골라주개냥 백엔드

## 요구사항
- Java 25
- Docker / Docker Compose (v2.20+)

## API Gateway DEV 실행 계약

`platform/api-gateway`는 로컬 프로필의 Route를 그대로 보존하고, Kubernetes DEV에서는
`infra` 프로필과 환경변수로 목적지를 주입한다. Endpoint 값이 누락되면 localhost로
대체하지 않고 애플리케이션 시작이 실패하도록 의도했다.

### 필수 환경변수

| 변수 | DEV 값/주입 방식 | 비밀값 |
| --- | --- | --- |
| `SPRING_PROFILES_ACTIVE` | `infra` | 아니요 |
| `AUTH_SERVICE_URL` | `https://generic-service.auth-service.svc.cluster.local:8443` | 아니요 |
| `MEMBER_SERVICE_URL` | `https://generic-service.member-service.svc.cluster.local:8443` | 아니요 |
| `PRODUCT_SERVICE_URL` | `https://generic-service.product-service.svc.cluster.local:8443` | 아니요 |
| `ORDER_SERVICE_URL` | `https://generic-service.order-service.svc.cluster.local:8443` | 아니요 |
| `PAYMENT_SERVICE_URL` | `https://generic-service.payment-service.svc.cluster.local:8443` | 아니요 |
| `REVIEW_SERVICE_URL` | `https://generic-service.review-service.svc.cluster.local:8443` | 아니요 |
| `NOTIFICATION_SERVICE_URL` | `https://generic-service.notification-service.svc.cluster.local:8443` | 아니요 |
| `JWT_JWKS_URI` | `https://generic-service.auth-service.svc.cluster.local:8443/oauth2/jwks` | 아니요 |
| `REDIS_HOST` | `redis-master.redis.svc.cluster.local` | 아니요 |
| `REDIS_PORT` | `6379` | 아니요 |
| `INTERNAL_GATEWAY_SECRET` | Kubernetes Secret `gateway-secret`의 같은 이름 키 | 예 |

`INTERNAL_GATEWAY_SECRET`의 원본은 기존 서비스들과 같은
`petflow/internal/gateway-secret`을 사용한다. 값 자체를 values나 로그에 기록하지 않는다.

### 연결별 TLS 계약

- ALB → Gateway: HTTP `8080`. 외부 TLS는 Public ALB에서 종료한다. Gateway 서버에
  `SERVER_SSL_*`를 주입하지 않는다.
- Gateway → Backend: HTTPS/mTLS `8443`. Spring Cloud Gateway Netty client가
  `internalmtls` SSL Bundle을 사용한다.
- Gateway → JWKS: Auth의 `/oauth2/jwks`를 별도 WebClient로 조회하며 같은
  `internalmtls` Bundle을 적용한다.
- Gateway → Redis: TLS/mTLS `6379`. 비밀번호/ACL은 현재 DEV Redis에서 사용하지 않는다.

Gateway Pod에는 cert-manager `internal-ca-issuer`가 발급한 client-auth 인증서 Secret을
다음 경로로 읽기 전용 마운트해야 한다.

| 파일 | 용도 |
| --- | --- |
| `/etc/mtls/tls.crt` | Gateway client certificate |
| `/etc/mtls/tls.key` | Gateway private key |
| `/etc/mtls/ca.crt` | Backend/JWKS/Redis 신뢰 CA |

인증서 SAN 검증을 유지하며 trust-all 및 hostname verification 비활성화는 사용하지 않는다.
cert-manager가 Secret을 갱신한 뒤 Gateway outbound client와 Redis/JWKS client의 자동
reload는 보장하지 않으므로 Pod rolling restart로 새 인증서를 로드한다.

### Route

| 외부 경로 | 대상 |
| --- | --- |
| `/api/v1/auths/**` | Auth |
| `/api/auth/oauth2/authorization/**`, `/api/auth/login/oauth2/code/**` | Auth OAuth |
| `/api/v1/members/**`, `/api/v1/pets/**` | Member |
| `/api/v1/products/**`, `/api/v1/time-deals/**` | Product |
| `/api/v1/orders/**`, `/api/v1/carts/**` | Order |
| `/api/v1/payments/**` | Payment |
| `/api/v1/reviews/**` | Review |
| `/api/v1/notifications/**` | Notification |

경로는 Backend Controller와 기존 local Route를 보존하며 `StripPrefix`나
`RewritePath`를 적용하지 않는다. 공개 API와 인증 정책도 이번 변경에서 바꾸지 않는다.

### 빌드

```bash
./gradlew :platform:api-gateway:test --no-daemon
./gradlew :platform:api-gateway:bootJar --no-daemon
docker build -f platform/api-gateway/Dockerfile -t api-gateway:local .
```

Dockerfile은 Jenkins가 먼저 만든 `platform/api-gateway/build/libs/*.jar`를 복사하므로,
단독 `docker build` 전에는 반드시 `bootJar`를 실행한다.

### 배포 전 선행조건

Jenkins는 `api-gateway` 변경을 감지해 테스트·bootJar·이미지 빌드·스캔 경로까지
지원한다. 그러나 아래 항목은 이 저장소의 이번 변경 범위가 아니며 준비되기 전에
`dev`에 병합하면 실제 배포 단계가 실패할 수 있다.

- ECR `297165773875.dkr.ecr.ap-northeast-2.amazonaws.com/petflow/api-gateway`
- `api-gateway` Namespace, client certificate와 `gateway-secret` 전달
- `gitops-value/values/dev/services/api-gateway/values.yaml`
- Gateway egress 및 Backend/Redis ingress NetworkPolicy
- Public ALB의 `/api/v1`, `/api/auth` Gateway 규칙
