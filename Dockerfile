# Dockerfile focused on production use case
# Builder stage needs JDK and gradle
FROM openjdk:17-jdk-alpine as builder
WORKDIR /root
COPY . .
RUN ./gradlew build

# Runner stage only needs JRE and JAR
FROM eclipse-temurin:17-jre-alpine
WORKDIR /root
COPY --from=builder /root/build/libs/*.jar ./app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","./app.jar"]