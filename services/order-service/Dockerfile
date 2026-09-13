FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /workspace
COPY . .
RUN chmod +x gradlew
ENV GRADLE_OPTS="-Xmx1g"
# --mount=type=cache는 BuildKit 전용 문법이라 Kaniko(Jenkinsfile의 빌드 도구)가
# 지원 안 함 — 붙어있으면 이미지 빌드 자체가 실패함(2026-09-13 CodeRabbit 리뷰로 발견).
RUN ./gradlew :services:order-service:bootJar -x test --no-daemon

FROM eclipse-temurin:25-jre-alpine
RUN addgroup -S appgroup && adduser -S --ingroup appgroup -H appuser
WORKDIR /app
COPY --from=build --chown=appuser:appgroup /workspace/services/order-service/build/libs/*.jar app.jar
USER appuser
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
EXPOSE 8080
