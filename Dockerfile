FROM maven:3.8.6-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY wait-for-it.sh /app/wait-for-it.sh
RUN apt-get update && \
    apt-get install -y default-mysql-client && \
    rm -rf /var/lib/apt/lists/* && \
    chmod +x /app/wait-for-it.sh
EXPOSE 8080
ENTRYPOINT ["/app/wait-for-it.sh", "mysql", "--", "java", "-jar", "/app/app.jar"]
