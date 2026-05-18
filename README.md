# El Culo — Servidor

Backend del juego **El Culo** (Spring Boot 3, Java 21, WebSocket STOMP, arquitectura hexagonal).

Repositorio del cliente: despliega por separado el frontend y apunta `VITE_WS_URL` a este servidor.

## Requisitos

- Java 21
- Maven 3.9+

## Arranque local

```bash
mvn spring-boot:run
```

API en `http://localhost:8080`, WebSocket SockJS en `http://localhost:8080/ws`.

## Tests

```bash
mvn test
```

## Build JAR

```bash
mvn -DskipTests package
java -jar target/culo-server-*.jar
```

## CORS y producción

En `src/main/resources/application.yml`, añade el origen de tu frontend:

```yaml
culo:
  cors:
    allowed-origins:
      - http://localhost:5173
      - https://tudominio.com
```

Vuelve a desplegar tras cambiar orígenes.

## Despliegue

Este proyecto **no** corre en SiteGround ni Vercel. Usa un servicio con JVM, por ejemplo:

- [Render](https://render.com)
- [Railway](https://railway.app)
- [Fly.io](https://fly.io)
- VPS con Java 21

Configuración típica:

- **Build:** `mvn -DskipTests package`
- **Start:** `java -jar target/culo-server-0.1.0.jar` (ajusta la versión del JAR)
- **Puerto:** el que exponga la plataforma (`PORT` / 8080)

## WebSocket

- SockJS: `/ws`
- Cliente → `/app/room.create`, `/app/room.join`, `/app/game.play`, …
- Servidor → `/topic/room/{code}/…`, `/topic/client/{clientId}/…`

Contrato OpenAPI: `src/main/resources/openapi/culo-ws-messages.yaml`
