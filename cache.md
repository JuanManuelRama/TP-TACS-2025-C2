# Cache

Para la capa de caché decidimos utilizar Redis, aprovechando su popularidad, soporte en la industria y facilidad de integración.

## Expiración

Dado que los atributos principales de Usuario y Evento son esencialmente estáticos (no cambian el nombre, descripción, etc.), optamos por no asignar expiración a sus entradas en caché. En caso de ser necesario, se dispone de una función explícita para invalidar claves.
Aun así, Redis está configurado con un límite de memoria, y al alcanzarse se aplica la política LRU (Least Recently Used) para desalojar entradas automáticamente.

## Estrategia

Adoptamos el patrón Cache-Aside por su simpleza: la caché se usa exclusivamente como optimización de rendimiento y no participa en la lógica de negocio.

Por ejemplo, aunque técnicamente podríamos cachear datos relacionados con inscripciones (como el cupo restante) aprovechando operaciones atómicas de Redis, decidimos no coordinar estado entre dos fuentes (DB + cache). Consideramos que la complejidad extra no justifica el beneficio marginal.
Como consecuencia, el sistema puede funcionar normalmente incluso si Redis está caído.

## Almacenamiento

Dado que las entidades son simples, se serializan como JSON en texto plano dentro de Redis. En un escenario de mayor escala o demanda de eficiencia, podría evaluarse un formato binario más compacto o un esquema más especializado.
