# 1. Start with a lightweight, official Java 17 Linux machine
FROM eclipse-temurin:17-jdk-alpine

# 2. Create a folder inside the container for our app
WORKDIR /app

# 3. Copy our compiled Spring Boot JAR file into the container
COPY target/*.jar app.jar

# 4. Open the vault door (Port 8080) so the internet can reach it
EXPOSE 8080

# 5. The exact terminal command the container runs when it wakes up
ENTRYPOINT ["java", "-jar", "app.jar"]