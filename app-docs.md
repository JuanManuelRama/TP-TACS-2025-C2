# APP Documentation

## Layout

Una pequeña barra en la parte de arriba de la página, si estamos logueados muestra nuestro nombre y da la opción de cerrar sesión (tras lo cual se refresca la página). Si no cuenta con los botones de [register](#authregister) y [login](#authlogin).

## Routes

### `/events`

Lista todos los eventos del sistema.

### `/events/:id`

Muestra el detalle sobre un evento, cuenta con comportamiento diferenciado para el organizador que para un usuario común.

#### Vista Organizador

Lista todos los inscriptos al evento, con la opción de echarlos. Además, cuenta con un botón para eliminar el evento en sí.

#### Vista Usuario

Existe el botón para subscribirse al evento, tras lo cual podemos quedar confirmados o en espera. Si ya estamos inscriptos el botón servira para desubscribirse. Si no estuviésemos logueados al interactuar con el mismo redirige hacía [login](#authlogin).

### `/auth/login`

Ruta para iniciar sesión en el sistema, tras un inicio correcto deja en LocalStorage la jwt, el username y el userId.

### `/auth/register`

Permite generar un nuevo usuario, luego inicia sesión automáticamente.

### `/profile`

Muestra información sobre el usuario, por ahora es solo el nombre. En caso de no estar logueado redirige a [login](#authlogin).