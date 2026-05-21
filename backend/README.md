# Backend

## Build & Run

The backend can be run locally in two different ways:

### Gradlew

Compile and run the server directly:

```bash
./gradlew build
./gradlew run
```

### JAR

Generate an executable JAR:

```bash
./gradlew shadowJar
java -jar build/libs/TP-TACS-2025-C2-all.jar
```

## 📖 API Documentation

### 🔹 Available Endpoints

| Method | Endpoint                            | Description                                                              | Protected | Parameters                    |
|:------:|-------------------------------------|--------------------------------------------------------------------------|:---------:|-------------------------------|
|  GET   | `/usuarios`                         | Lists all users                                                          |     ❌     |                               |
|  GET   | `/usuarios/{id}`                    | Retrieves a user                                                         |     ❌     | `id: UUID`                    |
|  POST  | `/usuarios`                         | Creates a new user                                                       |     ❌     | JSON body                     |
|  POST  | `/usuarios/login`                   | Returns a JWT if the credentials are valid                               |     ❌     | JSON body                     |
|  GET   | `/usuarios/eventos`                 | Lists all events organized by and joined by a user                       |    ✔️     | `id: UUID`                    |
|  GET   | `/eventos`                          | Lists all events                                                         |     ❌     |                               |
|  GET   | `/eventos/{id}`                     | Retrieves an event                                                       |     ❌     | `id: UUID`                    |
| DELETE | `/eventos/{id}`                     | Deletes an event                                                         |    ✔️     | `id: UUID`                    |
|  GET   | `/eventos/{id}/estadisticas`        | Retrieves statistics for an event                                        |     ❌     | `id: UUID`                    |
|  POST  | `/eventos`                          | Creates a new event                                                      |    ✔️     | JSON body                     |
|  GET   | `/eventos/{id}/inscriptos`          | Lists all users registered for an event                                  |    ✔️     | `id: UUID`                    |
|  POST  | `/eventos/{id}/inscriptos`          | Registers the authenticated user for an event                            |    ✔️     | `id: UUID`                    |
| DELETE | `/eventos/{id}/inscriptos`          | Cancels the authenticated user's registration                            |    ✔️     | `id: UUID`                    |
|  GET   | `/eventos/{id}/inscriptos/{userId}` | Retrieves a specific user's registration for an event                    |    ✔️     | `id: UUID`, `usuarioId: UUID` |
| DELETE | `/eventos/{id}/inscriptos/{userId}` | Cancels a specific user's registration                                   |    ✔️     | `id: UUID`, `usuarioId: UUID` |

Protected routes require an `Authorization: Bearer <token>` header obtained from `usuarios/login`.

### 💡 Possible Endpoints

| Method | Endpoint         | Description     | Parameters |
|--------|------------------|-----------------|------------|
| DELETE | `/usuarios/{id}` | Deletes a user  | `id: UUID` |

## Testing

There are no unit tests, since the core business logic is very simple (mostly CRUD operations).

Tests are performed directly against the endpoints. Each test assumes the rest of the application works correctly, except for the endpoint currently being tested.

To prepare the environment, the tests interact directly with the repositories using ephemeral containers.

## Design Decisions

The persistence layer is partially coupled with the rest of the application. Its logic is encapsulated within global `Repo` objects, while its configuration is handled separately in its own function.

There is also a lightweight service layer, mainly used to coordinate the database and cache. However, it is not strictly enforced: when an endpoint is simple enough, the logic is implemented directly in the controller.