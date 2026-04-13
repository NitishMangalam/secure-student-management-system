# Use JDK 21/23 base image
FROM eclipse-temurin:21-jdk-jammy

# Set working directory inside the container
WORKDIR /app

# Copy the generated JAR file from your target folder
COPY target/*.jar app.jar

# Expose the port your service runs on
EXPOSE 8082

# Command to run the service
ENTRYPOINT ["java", "-jar", "app.jar"]