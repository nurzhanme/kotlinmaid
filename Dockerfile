# Stage 1: Build the Kotlin application with Gradle
FROM gradle:8.2-jdk17-alpine AS builder

WORKDIR /app
COPY build.gradle.kts settings.gradle.kts /app/
COPY src /app/src

RUN ./gradlew build

# Stage 2: Create a lightweight image to run the application
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar /app/app.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","./app.jar"]