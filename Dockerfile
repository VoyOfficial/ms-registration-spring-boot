## Builder image
FROM maven:3.8.1-jdk-11 AS builder
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package

## Runner image
FROM openjdk:11-slim
MAINTAINER matheuscarv69
COPY --from=builder /usr/src/app/target/ms-registration-spring-boot-0.0.1.jar /usr/app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/app/app.jar"]