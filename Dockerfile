# Etapa 1: Compilación
FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /app
# Copiar archivos necesarios para compilar
COPY pom.xml .
COPY src ./src
# Compilar y crear el JAR
RUN mvn clean package -DskipTests
# Etapa 2: Construcción para stage
FROM eclipse-temurin:17-jre
WORKDIR /app
# Copiar el JAR desde la etapa anterior
COPY --from=build /app/target/emqx-to-rabbit-0.0.1-SNAPSHOT.jar app.jar
# Exponer el puerto
EXPOSE 8080
# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]