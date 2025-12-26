FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY gestionale-web/ ./gestionale-web/
WORKDIR /app/gestionale-web
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/gestionale-web/target/gestionale-web-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
