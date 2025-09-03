# API - TP-TACS-2025-C2

Este documento describe los endpoints disponibles en la API, incluyendo métodos, URLs, cuerpos de request y ejemplos de respuesta.

---

## Base URL

```
http://localhost:8080
```

---

## Endpoints

### 1. Registro de usuario

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

- **409 Conflict** si el username ya existe:

```json
{
  "error": "El nombre de usuario ya existe"
}
```

---

### 2. Login

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
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

- **401 Unauthorized** si usuario o contraseña incorrectos:

```json
{
  "error": "Usuario o contraseña incorrectos"
}
```

---

### 3. Obtener usuario por ID

- **URL:** `/usuarios/{id}`
- **Método:** `GET`
- **Descripción:** Devuelve los datos de un usuario por su ID. TODO: Requerir token JWT en el header.


#### Response

- **200 OK**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "pepito",
  "type": "PARTICIPANTE"
}
```

- **404 Not Found** si no existe:

```json
{
  "error": "Usuario with id 123e4567-e89b-12d3-a456-426614174000 not found"
}
```

- **401 Unauthorized** si el token es inválido o expiró.

---

### 4. Crear un evento

- **URL:** `/eventos`
- **Método:** `POST`
- **Descripción:** Le permite a un organizador **loggeado** crear un evento. Requiere JWT.

#### Headers

```
Authorization: Bearer <token>
```

Se obtiene el token en `/login`

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
- **401 Unauthorized** si el token es inválido o expiró.

---

## Notas

- Todos los endpoints que devuelven datos de usuario **no deben incluir la contraseña ni el hash**.
- Los JWT expiran luego de 10 horas por defecto.

---

## Cómo agregar un nuevo endpoint

1. Crear la ruta en `UsuarioRoutes.kt`.
2. Definir DTOs de request/response.
3. Agregar sección al README siguiendo el mismo formato:

```
### N. Nombre del endpoint
- URL:
- Método:
- Descripción:
- Request Body:
- Response:
```
