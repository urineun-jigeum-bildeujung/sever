FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace
COPY . .
RUN chmod +x gradlew
ENV GRADLE_OPTS="-Xmx1g"
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew :services:payment-service:bootJar -x test --no-daemon

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /workspace/services/payment-service/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
