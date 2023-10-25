# Stage 1: Build the Kotlin application with Gradle
FROM openjdk:17-jdk AS builder

WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew build --no-daemon

# Stage 2: Create a lightweight image to run the application
FROM amazoncorretto:17-alpine3.18

WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar /app/app.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","./app.jar"]