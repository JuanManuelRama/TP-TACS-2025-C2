# --- Stage 1: Build the JAR ---
FROM gradle:9.0.0-jdk17 AS builder
WORKDIR /home/gradle/src

# Copiamos archivos de configuración primero para cache de dependencias
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle

# Instalamos dependencias
RUN gradle build -x test --no-daemon || return 0

# Copiamos el resto del proyecto
COPY . .

# Construimos fat JAR con shadowJar
RUN gradle shadowJar --no-daemon

# --- Stage 2: Runtime ---
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copiamos el JAR desde la etapa de build
COPY --from=builder /home/gradle/src/build/libs/*-all.jar app.jar

# Exponemos el puerto que Ktor usará
EXPOSE 8080

# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "app.jar"]
