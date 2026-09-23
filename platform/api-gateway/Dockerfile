FROM eclipse-temurin:25-jre-alpine

ARG OTEL_AGENT_VERSION=2.31.1
ARG OTEL_AGENT_SHA256=bbf83c151b6400709e2f225bdd07a04f839d9d13b8b93464241333fd25d3e3ba

RUN addgroup -S appgroup && adduser -S --ingroup appgroup -H appuser
WORKDIR /app
COPY --chown=appuser:appgroup platform/api-gateway/build/libs/*.jar app.jar
RUN wget -O /app/opentelemetry-javaagent.jar \
      "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_AGENT_VERSION}/opentelemetry-javaagent.jar" \
    && test -s /app/opentelemetry-javaagent.jar \
    && echo "${OTEL_AGENT_SHA256}  /app/opentelemetry-javaagent.jar" | sha256sum -c - \
    && chown appuser:appgroup /app/opentelemetry-javaagent.jar \
    && chmod 0444 /app/opentelemetry-javaagent.jar

USER appuser
ENTRYPOINT ["java", "-javaagent:/app/opentelemetry-javaagent.jar", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
EXPOSE 8080
