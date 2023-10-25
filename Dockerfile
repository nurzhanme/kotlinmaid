# Stage 1: Build the Kotlin application with Gradle
FROM openjdk:17-jdk AS builder

WORKDIR /app
COPY . .
RUN ./gradlew build --no-daemon

# Stage 2: Create a lightweight image to run the application
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar /app/app.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","./app.jar"]