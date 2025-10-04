# Backend

## Build & Run

El backend puede ser ejecutado localmente de 2 formas distintas:

### Gradlew

Compilar y ejecutar el servidor directamente:

```bash
./gradlew build
./gradlew run 
```

### JAR

Generar un jar ejecutable:

```bash
./gradlew shadowJar
java -jar build/libs/TP-TACS-2025-C2-all.jar
```

## 📖 Documentación de la API

### 🔹 Endpoints disponibles

| Método | Endpoint                            | Descripción                                      | Protegida | Parámetros                    |
|:------:|-------------------------------------|--------------------------------------------------|:---------:|-------------------------------|
|  GET   | `/usuarios`                         | Lista todos los usuarios                         |     ❌     |                               |
|  GET   | `/usuarios/{id}`                    | Obtiene un usuario                               |     ❌     | `id: UUID`                    |
|  POST  | `/usuarios`                         | Crea un nuevo usuario,                           |     ❌     | JSON body                     |
|  POST  | `/usuarios/login`                   | Devuelve un JWT si los datos son válidos         |     ❌     | JSON body                     |
|  GET   | `/eventos`                          | Lista todos los eventos                          |     ❌     |                               |
|  GET   | `/eventos/{id}`                     | Obtiene un evento                                |     ❌     | `id: UUID`                    |
| DELETE | `/eventos/{id}`                     | Borra un evento                                  |    ✔️     | `id: UUID`                    |
|  GET   | `/eventos/{id}/estadisticas`        | Obtiene las estadísticas de un evento            |     ❌     | `id: UUID`                    |
|  POST  | `/eventos`                          | Crea un nuevo evento                             |    ✔️     | JSON body                     |
|  GET   | `/eventos/{id}/inscriptos`          | Muestra todos los inscriptos en un evento        |    ✔️     | `id: UUID`                    |
|  POST  | `/eventos/{id}/inscriptos`          | Inscribirse a un evento                          |    ✔️     | `id: UUID`                    |
| DELETE | `/eventos/{id}/inscriptos`          | Cancela la inscripción                           |    ✔️     | `id: UUID`                    |
|  GET   | `/eventos/{id}/inscriptos/{userId}` | Muestra la inscripción de un usuario a un evento |    ✔️     | `id: UUID`, `usuarioId: UUID` |                               |
| DELETE | `/eventos/{id}/inscriptos/{userId}` | Cancela la inscripción de un usuario             |    ✔️     | `id: UUID`, `usuarioId: UUID` |

Las rutas protegidas requieren un header `Authorization Bearer` obtenido en `usuarios/login`. 

### 💡 Posibles endpoints

| Método | Endpoint                       | Descripción                                                | Parámetros |
|--------|--------------------------------|------------------------------------------------------------|------------|
| DELETE | `/usuarios/{id}`               | Borra un usuario                                           | `id: UUID` |
| GET    | `/usuarios/{id}/inscripciones` | Lista todos las inscripciones de un usuario                | `id: UUID` |
| GET    | `/usuarios/{id}/eventos`       | Lista todos los eventos organizados por un usuario         | `id: UUID` |

## Testing

No hay test unitarios, ya que el core de la lógica de negocio es muy simple (básicamente CRUD).

Los test están realizados sobre los endpoints, cada test asume que anda todo, salvo el endpoint que
está probando. Para preparar el entorno, llama directo a los repositorios.

## Decisiones de Diseño

La capa de persistencia está parcialmente acoplada al resto de la aplicación, toda su lógica está
encapsulada en los objetos globales de Repo, y su configuración en una función aparte.

No hay capa de servicio, ya que la lógica de negocio es muy simple, las operaciones son mayormente CRUD
así que ocurren directamente dentro de la base de datos.

