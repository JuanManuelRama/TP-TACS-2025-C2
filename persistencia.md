# Persistencia

Para persistir la información, decidimos utilizar MongoDb, por consigna debía ser NoSQL, y entre las opciones nos pareció la más adecuada. \
Mongo provee garantías de atomicidad y concurrencia para operaciones básicas ($push, $poll, $inc) sobre un documento, lo que permite asegurarnos que la cantidad de inscriptos nunca supere el límite.

## Sharding

La shard key para los usuarios es su id, esto tiene sentido ya que la mayoría de búsquedas son por id, pero para el caso de Login, la búsqueda se realiza por el nombre ingresado, por lo que debe buscar en todas los shards por separado para encontrar en cual se está.

Este problema no puede ser resuelto dentro del esquema actual (o realizamos sharding por id, o por username), siendo la alternativa registrar los nombres y contraseñas en otra colección, implementando otra db (una key-value cómo dínamo es eficiente para este caso) o utilizar un serivcio externo de login.

## Límite de inscriptos

Los documentos en Mongo tienen un hard limit de 16MB, los inscriptos y waitlisted actualmente están embedidos dentro del documetno principal de evento, por lo que eventualmente alcanzaríamos el límite, y todas las peticiones de inscripción fallarían. \
Esto no pasaría hasta ~ 400k inscriptos, pero igualmente debe ser tenido en cuenta.

## Relaciones

Mongo no es relacional, decidimos no almacenar relaciones embedidas entre usuario y evento, sino los ObjectId correspondientes. Mongo provee, mediante pipelines, formas de agregar la información durante la query. Sentimos que añde complejidad innecesaria, y preferimos realizarlo en queries separadas. Sí evitamos realizar N+1 queries para cosas como todos los organizadores de una lista de eventos.

Además, estas relaciones son a objetos "estáticos" (ni el nombre del usuario, ni la descripción del evento debería cambiar frecuentemente) por lo que una buena capa de cache debería agilizar este proceso.
