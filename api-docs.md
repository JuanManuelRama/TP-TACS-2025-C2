# API - TP-TACS-2025-C2

Este documento describe los endpoints disponibles en la API, incluyendo métodos, URLs, cuerpos de request y ejemplos de respuesta.

---

## Consideraciones previas

### Base URL

``` http
http://localhost:8080
```

### Errores

Ante un error, la API retornará el código de error correspondiente, y el siguiente JSON:

```json
{
  "error": "Some error message"
}
```

### Autorización

La autorización se realiza mediante JWT, para obtener la misma se debe llamar al endpoint [login](#login), y luego incluirla en el header de la siguiente forma:

```http
Authorization: Bearer <token>
```

En caso de intentar acceder a un Endpoint que requiera autorización, y no tener la JWT, se retornara `401 Unauthorized`

---

## Endpoints

### Índice

- [Registro de usuario](#registro-de-usuario)
- [Login](#login)
- [Obtener usuarios](#obtener-usuarios)
- [Obtener usuario por ID](#obtener-usuario-por-id)
- [Crear un evento](#crear-un-evento)
- [Obtener eventos](#obtener-eventos)
- [Obtener evento](#obtener-evento)
- [Inscribirse](#inscribirse)
- [Ver Inscriptos](#ver-inscriptos)
- [Ver Inscripcion](#ver-inscripción)
- [Cancelar Inscripcion](#cancelar-inscripcion)
- [Cancelar Inscripcion de un Usuario](#cancelar-inscripcion-de-un-usuario)

### Registro de usuario

- **URL:** `/usuarios`
- **Método:** `POST`
- **Descripción:** Crea un nuevo usuario en el sistema.

#### Request Body

```json
{
  "username": "pepito",
  "password": "superSecreto123",
  "type": "PARTICIPANTE"
}
```

`type` debe ser uno de los siguientes valores: `PARTICIPANTE`, `ORGANIZADOR`.

#### Response

- **201 Created**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "pepito",
  "type": "PARTICIPANTE"
}
```

- **409 Conflict** si el username ya existe

---

### Login

- **URL:** `usuarios/login`
- **Método:** `POST`
- **Descripción:** Devuelve un JWT si el usuario y la contraseña son correctos.

#### Request Body

```json
{
  "username": "pepito",
  "password": "superSecreto123"
}
```

#### Response

- **200 OK**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "username": "pepito",
    "type": "participante"
  }
}
```

- **401 Unauthorized** si usuario o contraseña incorrectos

---

### Obtener usuarios

- **URL:** `/usuarios`
- **Método:** `GET`
- Autorización: TODO: Requerir token JWT en el header.
- **Descripción:** Devuelve los datos de todos los usuarios.

#### Response

- **200 OK**

```json
[
  {
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "pepito",
  "type": "PARTICIPANTE"
  }
]
```

- **401 Unauthorized** si el token es inválido o expiró

---

### Obtener usuario por ID

- **URL:** `/usuarios/{id}`
- **Método:** `GET`
- **Descripción:** Devuelve los datos de un usuario por su ID. TODO: Requerir token JWT

#### Response

- **200 OK**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "pepito",
  "type": "PARTICIPANTE"
}
```

- **404 Not Found** si no existe
- **401 Unauthorized** si el token es inválido o expiró

---

### Crear un evento

- **URL:** `/eventos`
- **Método:** `POST`
- **Descripción:** Le permite a un organizador crear un evento
- **Autorizacion**: Requiere [login](#login)

#### Request Body

```json
{
  "titulo": "Fiesta de prueba",
  "descripcion": "Evento de prueba para testear la API",
  "inicio": "2025-09-15T20:00:00",
  "duracion": 10800,
  "cupoMaximo": 100,
  "cupoMinimio": 10,
  "precio": 1500.5,
  "categorias": [
    "FIESTA",
    "CONCIERTO"
  ]
}
```

#### Response

- **201 Created**

```json
{
  "id" : "570g8400-e29b-41h4-a716-446655440040",
  "organizador": {
    "id": "ac0c2ba5-4139-46d2-b095-e38383a5d9fb",
    "username": "newUser-a4fb7cc8-5aa1-49ad-99ba-90d81880d68a",
    "type": "PARTICIPANTE"
  },
  "titulo": "Fiesta de prueba", 
  "descripcion": "Evento de prueba para testear la API", 
  "inicio": "2025-09-15T20:00:00", 
  "duracion": 10800, 
  "cupoMaximo": 100, 
  "cupoMinimio": 10, 
  "precio": 1500.5, 
  "categorias": [
    "FIESTA", 
    "CONCIERTO"
  ]
}
```

- **401 Unauthorized** si el token es inválido o expiró
- **404 Not Found** si el id de usuario de la jwt no se corresponde a un usuario en serio

---

### Obtener eventos

- **URL:** `/eventos`
- **Método:** `GET`
- **Query Params** TODO
- **Descripción** Obtiene todos los eventos

#### Response

- **200 OK**

```json
[
  {
    "id" : "570g8400-e29b-41h4-a716-446655440040",
    "organizador": {
      "id": "ac0c2ba5-4139-46d2-b095-e38383a5d9fb",
      "username": "newUser-a4fb7cc8-5aa1-49ad-99ba-90d81880d68a",
      "type": "PARTICIPANTE"
    },
    "titulo": "Fiesta de prueba",
    "descripcion": "Evento de prueba para testear la API",
    "inicio": "2025-09-15T20:00:00",
    "duracion": 10800,
    "cupoMaximo": 100,
    "cupoMinimio": 10,
    "precio": 1500.5,
    "categorias": [
    "FIESTA",
    "CONCIERTO"
    ]
  }
]
```

---

### Obtener evento

- **URL:** `/eventos/{id}`
- **Método:** `GET`
- **Descripción** Obtiene un evento

#### Response

- **200 OK**

```json
{
  "id" : "570g8400-e29b-41h4-a716-446655440040",
  "titulo": "Fiesta de prueba",
  "descripcion": "Evento de prueba para testear la API",
  "inicio": "2025-09-15T20:00:00",
  "duracion": 10800,
  "cupoMaximo": 100,
  "cupoMinimio": 10,
  "precio": 1500.5,
  "categorias": [
  "FIESTA",
  "CONCIERTO"
  ]
}
```

- **404 Not Found** Si no lo encontró

---

### Borrar un Evento

- **URL:** `/eventos/{id}`
- **Método:** `DELETE`
- **Descripción** Borra un evento
- **Autorizacion**: Requiere [login](#login) y ser el organizador del evento

#### Response

- **200 OK**
- **403 Forbidden**  Si alguien que no es el organizador intenta inscribirse
- **404 Not Found** Si no se encuentra el evento


---

### Inscribirse

- **URL:** `/eventos/{id}/inscriptos`
- **Método:** `POST`
- **Descripción** Se inscribe a un evento, puede quedar confirmado o en espera
- **Autorizacion**: Requiere [login](#login) y no ser el organizador del evento

#### Response

- **200 OK**

```json
{
  "usuario": "5ed2f21f-8943-44ce-94a1-27d9edac098c",
  "horaInscripcion": "2025-09-03T12:01:26.569835",
  "tipo": "CONFIRMACION"
}
```

o

```json
{
  "usuario": "5ed2f21f-8943-44ce-94a1-27d9edac098c",
  "horaInscripcion": "2025-09-03T12:01:26.569835",
  "tipo": "ESPERA"
}
```

- **403 Forbidden**  Si el organizador se intenta inscribir a su propio evento
- **404 Not Found** Si no encuentra el evento o el usuario de la JWT

---

### Ver Inscriptos

- **URL:** `/eventos/{id}/inscriptos`
- **Método:** `GET`
- **Descripción** Obtiene todos los inscriptos de un evento
- **Autorizacion**: Requiere [login](#login) y ser el organizador del evento

### Response

- **200 OK**

```json
[
  {
    "usuario": {
      "id": "ac0c2ba5-4139-46d2-b095-e38383a5d9fb",
      "username": "newUser-a4fb7cc8-5aa1-49ad-99ba-90d81880d68a",
      "type": "PARTICIPANTE"
    },
    "horaInscripcion": "2025-09-03T12:01:26.569835", 
    "tipo": "CONFIRMACION"
  }, 
  {
    "usuario": {
      "id": "ac0c2ba5-4139-46d2-b095-e38383a5d9fb",
      "username": "newUser-a4fb7cc8-5aa1-49ad-99ba-90d81880d68a",
      "type": "PARTICIPANTE"
    },
    "horaInscripcion": "2025-09-03T12:01:26.569835", 
    "tipo": "ESPERA"
  }
]
```

- **403 Forbidden** Si alguien que no es el organizador intenta acceder
- **404 Not Found** Si no se encuentra el evento, o el usuario de la JWT

---

### Ver Inscripción

- **URL:** `/eventos/{id}/inscriptos/{userId}`
- **Método:** `GET`
- **Descripción** Obtiene una inscripci´pn de un evento
- **Autorizacion**: Requiere [login](#login) y ser el organizador del evento o el usuario a ver

### Response

- **200 OK**

```json
{   
  "usuario": {
    "id": "ac0c2ba5-4139-46d2-b095-e38383a5d9fb",
    "username": "newUser-a4fb7cc8-5aa1-49ad-99ba-90d81880d68a",
    "type": "PARTICIPANTE"
  },
  "horaInscripcion": "2025-09-03T12:01:26.569835", 
  "tipo": "CONFIRMACION"
  }
```

- **403 Forbidden** Si alguien que no es el organizador o el usuario mismo intenta acceder
- **404 Not Found** Si no se encuentra el evento

---

### Cancelar Inscripcion

- **URL:** `/eventos/{id}/inscriptos`
- **Método:** `DELETE`
- **Descripción** Cancela la inscripción a un evento.
- **Autorizacion**: Requiere [login](#login)

#### Response

- **200 OK**
- **400 Bad Request** Si no estaba inscripto
- **404 Not Found** Si no se encuentra el evento o el usuario de la JWT

---

### Cancelar Inscripcion de un Usuario

- **URL:** `/eventos/{id}/inscriptos/{userId}`
- **Método:** `DELETE`
- **Descripción** Cancela la inscripción a un evento de un usuario.
- **Autorizacion**: Requiere [login](#login) y ser el organizador del evento

#### Response

- **200 OK**
- **400 Bad Request** Si no estaba inscripto
- **403 Forbidden** Si el usuario de la JWT no es el organizador
- **404 Not Found** Si no se encuentra el evento o el usuario de la JWT

## Notas

- Todos los endpoints que devuelven datos de usuario **no deben incluir la contraseña ni el hash**.
- Los JWT expiran luego de 10 horas por defecto.

---

## Cómo agregar un nuevo endpoint

1. Crear la ruta en `UsuarioRoutes.kt`.
2. Definir DTOs de request/response.
3. Agregar sección al README siguiendo el mismo formato:

```markdown
### Nombre del endpoint
- **URL:**
- **Método:**
- **Descripción:**
- **Autorizacion:**
#### Request Body
#### Response
```
