FROM eclipse-temurin:25-jre-alpine
# jar는 Jenkinsfile의 Build 스테이지에서 gradle 컨테이너가 미리 빌드해둠 — kaniko가
# 매번 전체 모노레포를 COPY해서 gradle을 다시 돌리면 서비스 7개가 캐시 락 경합/
# 디스크 초과로 이어졌음(2026-09-14 실제로 겪음). 여기서는 이미 만들어진 jar만 담는다.
RUN addgroup -S appgroup && adduser -S --ingroup appgroup -H appuser
WORKDIR /app
COPY --chown=appuser:appgroup services/product-service/build/libs/*.jar app.jar
USER appuser
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
EXPOSE 8080
