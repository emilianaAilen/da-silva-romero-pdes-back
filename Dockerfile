# Usamos una imagen oficial de Maven para construir la aplicación
FROM maven:3.9.5-eclipse-temurin-17 AS build
# Creamos el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el archivo pom.xml y Descargamos las dependencias necesarias (esto se almacena en la caché de Docker)
COPY pom.xml ./
RUN mvn dependency:go-offline

# Copiamos el código fuente de la aplicación dentro del contenedor
COPY src ./src

# Compilamos y empaquetamos la aplicación
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk

# Creamos el directorio de trabajo
WORKDIR /app

# Copiamos el archivo JAR de la etapa de construcción al contenedor de producción
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto en el que correrá la aplicación (ajusta según sea necesario)
EXPOSE 8080


# Argumentos de construcción para pasar valores desde el build
ARG MERCADO_LIBRE_TOKEN
ARG MERCADO_LIBRE_BASE_URL
ARG MERCADO_LIBRE_CLIENT_ID

# Variables de entorno que usan los valores pasados en el build
ENV MERCADO_LIBRE_TOKEN=${MERCADO_LIBRE_TOKEN}
ENV MERCADO_LIBRE_BASE_URL=${MERCADO_LIBRE_BASE_URL}
ENV MERCADO_LIBRE_CLIENT_ID=${MERCADO_LIBRE_CLIENT_ID}
ENV MERCADO_LIBRE_CLIENT_SECRET=${MERCADO_LIBRE_CLIENT_SECRET}

# Establecemos variables de entorno para la conexión a la base de datos
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/postgres
ENV SPRING_DATASOURCE_USERNAME=user
ENV SPRING_DATASOURCE_PASSWORD=pass

ENV SPRING_PROFILES_ACTIVE=dev 

# Ejecutamos el entrypoirnt con los comandos para levantar la APP
ENTRYPOINT ["java", "-jar", "app.jar"]