# Dockerfile focused on production use case
# Builder stage needs JDK and gradle
FROM openjdk:8-jdk-alpine as builder
WORKDIR /root
COPY . .
RUN ./gradlew build

# Runner stage only needs JRE and JAR
FROM openjdk:8-jre-alpine
WORKDIR /root
COPY --from=builder /root/build/libs/*.jar ./app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","./app.jar"]