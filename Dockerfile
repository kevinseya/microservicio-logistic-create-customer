# Usar una imagen base con OpenJDK
FROM openjdk:17-slim

# Instalar Maven
RUN apt-get update && apt-get install -y maven

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el archivo pom.xml y descargar las dependencias de Maven
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar el código fuente de la aplicación
COPY src /app/src

# Compilar y empaquetar la aplicación
RUN mvn clean package -DskipTests

# Exponer el puerto en el que la aplicación correrá
EXPOSE 80

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app/target/logistic_create_user-0.0.1-SNAPSHOT.jar"]
