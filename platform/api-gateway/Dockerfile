FROM eclipse-temurin:25-jre-alpine
RUN addgroup -S appgroup && adduser -S --ingroup appgroup -H appuser
WORKDIR /app
COPY --chown=appuser:appgroup platform/api-gateway/build/libs/*.jar app.jar

ADD --chown=appuser:appgroup https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.31.1/opentelemetry-javaagent.jar otel-javaagent.jar
RUN echo "bbf83c151b6400709e2f225bdd07a04f839d9d13b8b93464241333fd25d3e3ba  otel-javaagent.jar" | sha256sum -c -

USER appuser
ENTRYPOINT ["java", "-javaagent:/app/opentelemetry-javaagent.jar", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
EXPOSE 8080
