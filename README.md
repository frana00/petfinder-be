# PetSignal Backend

API REST para gestión de alertas de mascotas perdidas y encontradas.

> **Créditos**: Proyecto iniciado por [Jana (Hannah-bannanah)](https://github.com/Hannah-bannanah). 
> Repositorio original: [petsignal-be](https://github.com/Hannah-bannanah/petsignal-be)

## Tecnologías

- Java 21
- Spring Boot 3.4.5
- Spring Security + JWT
- MySQL 8.0
- AWS S3
- Docker
- OpenAPI 3.0

## Requisitos

- Java 21+
- Maven 3.6+
- MySQL 8.0+
- Docker 20.10+

## Configuración

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/yourusername/petsignal-be.git
   cd petfinder-be
   ```

2. Iniciar el contenedor MySQL de Docker
   - Actualizar `docker-compose.yml` con tus credenciales
   - Solo la primera vez: construir el contenedor
   ```bash
   cd database;
   docker compose up --build -d;
   cd ..
   ```
   - Para siguientes ejecuciones puedes iniciar el contenedor normalmente
   ```bash
   cd database;
   docker compose up -d;
   cd ..
   ```
   - Después de cerrar la aplicación, puedes detener el contenedor
   ```bash
   cd database;
   docker compose down;
   cd ..
   ```

3. Configurar la aplicación:
   - Crear archivo `.env` en la raíz del proyecto con tus credenciales
   - La aplicación cargará automáticamente las variables de entorno

4. Compilar la aplicación:
   ```bash
   mvn clean install
   ```

5. Ejecutar la aplicación:
   ```bash
   mvn spring-boot:run
   ```

Aplicación disponible en: `http://localhost:8080`

## Documentación API

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI**: `http://localhost:8080/openapi.yaml`

## Funcionalidades

- **Alertas**: CRUD de mascotas perdidas/encontradas
- **Posts**: Comentarios comunitarios en alertas
- **Usuarios**: Registro, autenticación, perfiles
- **Fotos**: Subida y gestión vía S3
- **Notificaciones**: Emails automáticos
- **Seguridad**: JWT + HTTP Basic Auth

## Estructura del Proyecto

```
src/main/java/com/petsignal/
├── alert/          # Gestión de alertas
├── auth/           # Autenticación
├── posts/          # Comentarios
├── user/           # Usuarios
├── photos/         # Gestión de fotos
├── notifications/  # Notificaciones
└── ...
```

## Licencia

MIT License - Ver archivo [LICENSE](LICENSE)
