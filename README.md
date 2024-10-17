# Asesor Personal de Compras ( integracion con API de MELI)

## Introduccion
La empresa Mercado Libre le solicita a una universidad pública el desarrollo de la aplicación web Asesor Personal de Compras (APC) para que ayude a los clientes a llevar a cabo sus compras con el objetivo de maximizar las ventas. Esta aplicación permitirá a los clientes consultar sobre los productos, ver sus características, precios, etc. También se podrá guardar como favoritos los productos que al cliente le interese y hacer un seguimiento del mismo. 

## Tecnologías Utilizadas

- Java 
- Spring Boot
- Spring Data JPA
- Postgresql (para desarrollo)
- Spring Security
- Docker

## Requisitos Previos

- Java 17 
- Maven (para construir y gestionar dependencias) version 3.9.5
- Docker (version version 26.1.4 o superior)

## Diagrama de solución

## Configuración del Proyecto

1. **Clonar el Repositorio:**
    ```bash
    git clone https://github.com/emilianaAilen/da-silva-romero-pdes-back
    cd da-silva-romero-pdes-back
    ```

2. **Instalacion de dependencias**
    ```bash
    mvn clean install -Dmercadolibre.api.token=<tu_token_meli> -Dmercadolibre.api.base-url=<meli.base-url>
    ```

2. **Correr la aplicacion**
    ```bash
    mvn spring-boot:run -Dspring-boot.run.profiles=dev 
    ```
    Tambien pueden usar el run el IDE a eleccion.

3. **Correr la Aplicacion mediante DOCKER-COMPOSE**
   1. **Corremos docker componse** 
        ```bash
        docker-compose up 
        ```
        Si solo queremos correr la app porque ya tenemos levantado postgrest usamos :
        ```bash
        docker-compose up --build app 
        ```
    2. Dar de baja el docker-compose
        ```bash
        docker-compose down
        ```
    De esta manera tendriamos levantada nuestra aplicacion con postgrest y la app configurada.

4. **Correr la aplicacion mediante DOCKER**
   1.  **Corremos docker Build**
        ```bash
        docker build -t <nombre-de-la-imagen-a-eleccion>.<version> .
        ```
        Ejemplo
        ```bash
        docker build -t apc-springboot-app.0.0.3 .
        ```
    2. **Verificamos la imagen id que nos genero el build**
        Para poder levantar la APP es necesario buscar la imagen id que nos genero el build, para eso hacemos lo siguiente.

         ```bash
        docker images
        ```

        Con ese comando podemos ver todas la imagenes que tenemos buildeadas, buscamos la imagen que coincida con la configuracion que le hicimos en el build. 
    3. **Levantamos la app con docker RUN**
        ```bash
        docker run -p 8080:8080 <imagen_id>
        ```
        Ejemplo
        ```bash
        docker run -p 8080:8080 46c17b01ee4e
        ```

# API REST de Usuarios

## Endpoints Disponibles

## Notas Adicionales
