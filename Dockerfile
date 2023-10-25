FROM openjdk:17-jdk-alpine as builder
WORKDIR /root
COPY . .
RUN ./gradlew build

# Runner stage only needs JRE and JAR
FROM openjdk:17-jre-alpine
WORKDIR /root
COPY --from=builder /root/build/libs/*.jar ./app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","./app.jar"]