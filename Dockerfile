# Stage 1: Build the Kotlin application with Gradle
FROM gradle:8.2-jdk17-alpine AS builder

WORKDIR /app
COPY build.gradle.kts settings.gradle.kts /app/
COPY src /app/src

RUN gradle build --no-daemon

# Stage 2: Create a lightweight image to run the application
FROM adoptopenjdk:17-jre-hotspot-bionic

WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar /app/app.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","./app.jar"]