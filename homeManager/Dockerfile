FROM maven:3.8.3-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Remplace 8080 si ton backend écoute sur un autre port
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]