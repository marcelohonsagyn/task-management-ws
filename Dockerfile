# Use official OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew ./gradlew
COPY gradle ./gradle
COPY build.gradle ./build.gradle
COPY settings.gradle ./settings.gradle
COPY src ./src

# Give execution permission to Gradle wrapper
RUN chmod +x gradlew

# Build the application
RUN ./gradlew clean build --no-daemon

# Expose the application port
EXPOSE 8080

# Run the application (ensure we have built the correct jar)
CMD ["java", "-jar", "build/libs/task-management-ws-1.0.0.jar"]