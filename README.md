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

Este proyecto **no** corre en SiteGround ni **Vercel**. Vercel solo sirve funciones serverless / estáticos; un Spring Boot con WebSocket STOMP necesita un proceso JVM siempre activo. Si despliegas aquí en Vercel verás `404: NOT_FOUND` en todas las rutas.

### Render (recomendado)

1. Sube el repo a GitHub (`breimato/culo-server`).
2. En [Render](https://render.com) → **New** → **Blueprint** y conecta el repo (usa el `render.yaml` incluido), o **Web Service** con runtime **Docker**.
3. Cuando esté en marcha, la URL será algo como `https://culo-server-xxxx.onrender.com`.
4. En el cliente, `VITE_WS_URL=https://culo-server-xxxx.onrender.com/ws` y vuelve a `pnpm deploy:ftp`.

Health check: `GET /health` → `{"status":"ok"}`.

### Otras plataformas JVM

- [Railway](https://railway.app)
- [Fly.io](https://fly.io)
- VPS con Java 21 + `Dockerfile` del repo

Configuración típica:

- **Build:** `mvn -DskipTests package` (o Docker)
- **Start:** `java -jar target/culo-server-0.1.0.jar`
- **Puerto:** variable `PORT` (por defecto 8080)

## WebSocket

- SockJS: `/ws`
- Cliente → `/app/room.create`, `/app/room.join`, `/app/game.play`, …
- Servidor → `/topic/room/{code}/…`, `/topic/client/{clientId}/…`

Contrato OpenAPI: `src/main/resources/openapi/culo-ws-messages.yaml`
