# --- Stage 1: Build the JAR ---
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /app

# Copiar el wrapper de gradle + configuraciones
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle.kts settings.gradle.kts gradle.properties ./

# Descargar dependencias (aparte para cache)
RUN ./gradlew dependencies --no-daemon || return 0

# Copy the rest of the backend source
COPY . .

# Construir el fat JAR
RUN ./gradlew shadowJar --no-daemon

# --- Stage 2: Runtime ---
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copiar el JAR
COPY --from=builder /app/build/libs/*-all.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]