FROM eclipse-temurin:25-jre-alpine
# jar는 Jenkinsfile의 Build 스테이지에서 gradle 컨테이너가 미리 빌드해둠 — kaniko가
# 매번 전체 모노레포를 COPY해서 gradle을 다시 돌리면 서비스 7개가 캐시 락 경합/
# 디스크 초과로 이어졌음(2026-09-14 실제로 겪음). 여기서는 이미 만들어진 jar만 담는다.
RUN addgroup -S appgroup && adduser -S --ingroup appgroup -H appuser
WORKDIR /app
COPY --chown=appuser:appgroup services/review-service/build/libs/*.jar app.jar

# 분산 추적(Tempo)용 OpenTelemetry Java 계측 에이전트. GitHub Release에는 체크섬
# 파일이 따로 없어서(.asc GPG 서명만 제공, 검증 인프라까지 갖추긴 과함) 최초
# 다운로드 시점에 직접 계산한 sha256을 고정 — 이후 빌드마다 이 값과 대조해서
# 최소한 배포 후 조용히 바뀌지 않았는지는 보장한다(2026-09-15).
ADD --chown=appuser:appgroup https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.31.1/opentelemetry-javaagent.jar otel-javaagent.jar
RUN echo "bbf83c151b6400709e2f225bdd07a04f839d9d13b8b93464241333fd25d3e3ba  otel-javaagent.jar" | sha256sum -c -
USER appuser
ENTRYPOINT ["java", "-javaagent:/app/otel-javaagent.jar", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
EXPOSE 8080
