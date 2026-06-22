# ==========================================
# STAGE 1: Build the Java Application
# ==========================================
FROM eclipse-temurin:17-jdk-alpine AS builder

# Create a working directory
WORKDIR /app

# Copy all your source code into the container
COPY . .

# Grant permission to the Maven wrapper and build the app
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# ==========================================
# STAGE 2: Run the Java Application
# ==========================================
FROM eclipse-temurin:17-jdk-alpine

# Create the final working directory
WORKDIR /app

# Copy ONLY the built JAR file from Stage 1 (leaving all the heavy source code behind)
COPY --from=builder /app/target/*.jar app.jar

# Open the vault door (Port 8080)
EXPOSE 8080

# The exact terminal command to wake up the app
ENTRYPOINT ["java", "-jar", "app.jar"]