# Use an official Gradle image with Java 21
FROM gradle:8-jdk21-alpine AS build

# Set the working directory
WORKDIR /build

# Copy the Gradle wrapper files
COPY gradlew ./
COPY gradle ./gradle

# Copy the build.gradle and settings.gradle (if you have one) files
COPY build.gradle ./
COPY settings.gradle ./

# Download dependencies
# This is done as a separate layer to leverage Docker's build cache
RUN ./gradlew dependencies --no-daemon

# Copy the rest of the source code
COPY src ./src

# Build the application, skipping tests
RUN ./gradlew build -x test --no-daemon

# Use a minimal JRE image for a smaller footprint
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy the built JAR from the 'build' stage
# Gradle builds the JAR in build/libs/
COPY --from=build /build/build/libs/*.jar ./app.jar

# Expose the application port
EXPOSE 8080

# The command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]


