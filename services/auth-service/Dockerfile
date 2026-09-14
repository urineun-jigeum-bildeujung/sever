FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace
COPY . .
RUN chmod +x gradlew
ENV GRADLE_OPTS="-Xmx1g"
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew :services:auth-service:bootJar -x test --no-daemon

FROM eclipse-temurin:25-jre
RUN groupadd --system appgroup && useradd --system --gid appgroup --no-create-home appuser
WORKDIR /app
COPY --from=build --chown=appuser:appgroup /workspace/services/auth-service/build/libs/*.jar app.jar
USER appuser
ENTRYPOINT ["java", "-jar", "app.jar"]