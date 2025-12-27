## Builder image
FROM maven:3.8.7-eclipse-temurin-17 AS builder
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package -Dmaven.test.skip=true

## Runner image
FROM eclipse-temurin:17-jdk-jammy
MAINTAINER matheuscarv69
COPY --from=builder /usr/src/app/target/ms-registration-spring-boot-0.0.2.jar /usr/app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/app/app.jar"]
