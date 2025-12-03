#!/bin/bash

#Variables
APP_NAME="emqx-to-rabbit" #El nombre de la app
DOCKER_TAG="bitiot21/$APP_NAME"
IMAGE_VERSION="1.0.1" # En vez de versiones, se puede usar latest, pero este será el versionamiento del proyecto, entonces así

echo "Iniciando build y push para $APP_NAME..."

# 1. Construyendo el JAR universal con Maven
echo "Paso 1. Compilando el proyecto con Maven..."
mvn clean package -DskipTests || { echo "Error en la compilación con Maven"; exit 1; }

# 2. Construir la imagen Docker
echo "Paso 2. Construyendo la imagen Docker: $DOCKER_TAG:$IMAGE_VERSION"z
# Usamos el Dockerfile
docker build -t $DOCKER_TAG:$IMAGE_VERSION . || { echo "Error al construir la imagen Docker"; exit 1; }

# 3. Subir la imagen a Docker Hub
echo " Paso 3. Haciendo PUSH de la imagen Docker a Docker Hub..."
docker push $DOCKER_TAG:$IMAGE_VERSION || { echo "Error al subir la imagen a Docker Hub"; exit 1; }

echo "¡Proceso finalizado! Imagen $DOCKER_TAG:$IMAGE_VERSION subida."

read -p "Presiona enter para continuar..."