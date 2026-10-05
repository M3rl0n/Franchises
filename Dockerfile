# syntax=docker/dockerfile:1

# Etapa 1: compilar el jar
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Los tests se omiten porque necesitan la base de datos, que no existe durante el build
RUN --mount=type=cache,target=/root/.m2 mvn -B package -DskipTests

# Etapa 2: imagen final, solo JRE + jar
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
COPY --from=build /app/target/*.jar app.jar
USER spring
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
