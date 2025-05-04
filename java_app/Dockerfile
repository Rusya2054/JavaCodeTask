FROM maven:3.9-eclipse-temurin-17-alpine AS build
COPY . /app
WORKDIR /app

RUN mvn clean package -Dmaven.test.skip
# RUN mvn clean test -Dspring.profiles.active=test package

FROM eclipse-temurin:17-jre-alpine
COPY --from=build /app/target/javacode.task-*.jar /app.jar


CMD ["java", "-jar", "app.jar"]
