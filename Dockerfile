## Builder image
FROM maven:3.8.7-eclipse-temurin-17 AS builder
WORKDIR /usr/src/app
COPY src ./src
COPY pom.xml ./
# .githooks is not copied into the image; skip install-git-hooks (validate phase).
RUN mvn -f pom.xml clean package -Dmaven.test.skip=true -Dskip.git.hooks=true

## Runner image
FROM eclipse-temurin:17-jdk-jammy
MAINTAINER matheuscarv69
COPY --from=builder /usr/src/app/target/ms-registration-spring-boot-0.0.2.jar /usr/app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/app/app.jar"]
