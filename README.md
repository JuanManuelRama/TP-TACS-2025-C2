# Trabajo Práctico TACS 2025 C2

## Build & Run

Existen 3 formas distintas de ejecutar el proyecto

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

### Docker 

Generando el contenedor

```bash
docker build -t ktor-app .
docker run -p 8080:8080 ktor-app
```

## 📖 Documentación de la API

### 🔹 Endpoints disponibles

| Método | Endpoint                            | Descripción                               | Protegida | Parámetros                    |
|:------:|-------------------------------------|-------------------------------------------|:---------:|-------------------------------|
|  GET   | `/usuarios`                         | Lista todos los usuarios                  |     ❌     |                               |
|  GET   | `/usuarios/{id}`                    | Obtiene un usuario                        |     ❌     | `id: UUID`                    |
|  POST  | `/usuarios`                         | Crea un nuevo usuario,                    |     ❌     | JSON body                     |
|  POST  | `/usuarios/login`                   | Devuelve un JWT si los datos son válidos  |     ❌     | JSON body                     |
|  GET   | `/eventos`                          | Lista todos los eventos                   |     ❌     |                               |
|  GET   | `/eventos/{id}`                     | Obtiene un evento                         |     ❌     | `id: UUID`                    |
|  POST  | `/eventos`                          | Crea un nuevo evento                      |    ✔️     | JSON body                     |
|  GET   | `/eventos/{id}/inscriptos`          | Muestra todos los inscriptos en un evento |    ✔️     | `id: UUID`                    |
|  POST  | `/eventos/{id}/inscriptos`          | Inscribirse a un evento                   |    ✔️     | `id: UUID`                    |
| DELETE | `/eventos/{id}/inscriptos`          | Cancela la inscripción                    |    ✔️     | `id: UUID`                    |
| DELETE | `/eventos/{id}/inscriptos/{userId}` | Cancela la inscripción de un usuario      |    ✔️     | `id: UUID`, `usuarioId: UUID` |

Las rutas protegidas requieren un header `Authorization Bearer` obtenido en `usuarios/login`. 

### 💡 Posibles endpoints

| Método | Endpoint                       | Descripción                                                | Parámetros |
|--------|--------------------------------|------------------------------------------------------------|------------|
| DELETE | `/usuarios/{id}`               | Borra un usuario                                           | `id: UUID` |
| GET    | `/usuarios/{id}/inscripciones` | Lista todos las inscripciones de un usuario                | `id: UUID` |
| GET    | `/usuarios/{id}/eventos`       | Lista todos los eventos organizados por un usuario         | `id: UUID` |
| GET    | `/eventos/{id}/estadisticas`   | Obtiene las estadísticas de un evento (tasa de conversión) | `id: UUID` |


## Testing

### Kotlin

Tenemos test de Kotlin, para corroborar lógica de dominio, los mismos se corren con

```bash
./gradlew test
```

### Postman

Para verificar que la API sea funcional, hay un script de Postman. No verifica los casos no felices, ni tampoco es exhaustivo. Pero sirve para ver la API en funcionamiento

## Decisiones de Diseño

### Manejo de errores

Los errores se tratan mediante `Result<T>`, lo que consideramos superior al manejo de errores por excepciones tradicionales, ya que:

+ Obliga a tener en cuenta siempre el caso de error.
+ Simplifica el testing, al poder validar explícitamente los estados fallidos.
+ Hace más predecible la propagación de fallos.

Buscamos, además, que si un objeto retorna `Result.failure` no tenga ningún efecto de lado.

### Data Transfer Objects

Las clases de dominio (`evento`, `usuario`, etc.) no son serializadas directamente \
En su lugar, se transforman a objetos DTO, con el objetivo de:

+ Separar el modelo interno del formato expuesto públicamente.
+ Mantener la flexibilidad de cambiar la estructura interna sin romper la API.

### Arquitectura lógica

No seguimos el modelo de capas rígido de `router->controller->service->domain`. 
En su lugar favorecemos objetos de dominio ricos en comportamiento \
Esto, junto con el uso de `Result<T>`, permite que la lógica de los endpoints sea sencilla: consiste principalmente en navegar los distintos estados de cada operación y ejecutar los métodos correspondientes del dominio.
