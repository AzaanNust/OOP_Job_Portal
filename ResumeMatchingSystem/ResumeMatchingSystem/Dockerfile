# ================================================================
# Dockerfile  –  Job Portal Spring Boot Backend
# ================================================================
# This file tells Docker how to build and run the Spring Boot app.
#
# Multi-stage build:
#   Stage 1 (builder): Uses Gradle to compile and package the JAR
#   Stage 2 (runtime): Uses a slim JRE image to run the JAR
#
# Why multi-stage?
#   The Gradle build tools (~500MB) are only needed during compilation.
#   The final image only contains the JRE + the compiled JAR (~200MB).
#   This keeps the production image small and secure.
# ================================================================

# ── Stage 1: Build the JAR ────────────────────────────────────
FROM gradle:8.10-jdk21 AS builder

# Set working directory inside the container
WORKDIR /app

# Copy Gradle config files first (Docker caches these layers)
# This way, if only source code changes, Gradle dependencies
# are NOT re-downloaded — significantly speeds up rebuilds
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle

# Download dependencies (cached unless build files change)
RUN gradle dependencies --no-daemon

# Now copy the actual source code
COPY src ./src

# Build the JAR, skip tests for faster image builds
RUN gradle bootJar --no-daemon -x test

# ── Stage 2: Run the JAR ──────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

# Create a non-root user for security (never run apps as root in production)
RUN addgroup -S jobportal && adduser -S jobportal -G jobportal

WORKDIR /app

# Copy only the compiled JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Change ownership to the non-root user
RUN chown jobportal:jobportal app.jar

# Switch to non-root user
USER jobportal

# Expose the port the app runs on
EXPOSE 8080
f
# JVM flags:
#   -XX:+UseContainerSupport  → JVM respects Docker memory limits
#   -XX:MaxRAMPercentage=75.0 → Use at most 75% of container RAM
#   -Djava.security.egd      → Faster startup (uses /dev/urandom for random)
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]