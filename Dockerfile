FROM gradle:8.5-jdk21 AS build

# Set working directory
WORKDIR /app

# Copy Gradle wrapper and build files for caching
COPY gradle/ gradle/
COPY gradlew gradlew
COPY build.gradle.kts settings.gradle.kts ./
COPY buildSrc/ buildSrc/

# Copy source code
COPY libs/ libs/
COPY services/ services/

# Build the application JAR
RUN ./gradlew build --no-daemon

FROM eclipse-temurin:21-jre

# Install dependencies for Deno and health checks
RUN apt-get update && apt-get install -y \
    curl \
    unzip \
    && rm -rf /var/lib/apt/lists/*

# Create non-root user
RUN groupadd -r connxt && useradd -r -g connxt connxt

# Create /app directory with proper ownership
RUN mkdir -p /app && chown -R connxt:connxt /app

# Switch to non-root user
USER connxt
WORKDIR /home/connxt

# Install Deno for the non-root user
RUN curl -fsSL https://deno.land/install.sh | sh
ENV DENO_INSTALL="/home/connxt/.deno"
ENV PATH="/home/connxt/.deno/bin:$PATH"

# Switch working directory for Spring Boot app
WORKDIR /app

# Copy the built JAR from build stage with correct ownership
COPY --from=build --chown=connxt:connxt /app/services/core/build/libs/*.jar app.jar

# Expose application ports
EXPOSE 8001
EXPOSE 8000

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8001/actuator/health || exit 1

# Entry point to run Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
