package com.g7.repo

import com.g7.evento.Evento
import com.g7.evento.EventoInputDto
import com.g7.evento.Inscripcion
import com.g7.evento.Inscriptos
import com.g7.routing.eventos.EventoParams
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.*
import org.bson.Document
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import java.time.LocalDateTime
import kotlin.collections.listOf

data class Inscripciones (
    val inscriptos: List<Inscripcion.Confirmada>? = emptyList(),
    val esperas: List<Inscripcion.Espera>? = emptyList()
)


object EventoRepo {
    private lateinit var collection: MongoCollection<Evento>
    private val projection = Projections.exclude("inscriptos", "esperas")

    fun init() {
        collection = MongoProvider.eventoCollection
    }

    fun getEventos(params: EventoParams = EventoParams()): List<Evento> {
        val filters = mutableListOf<Bson>()

        params.keywords?.let { it.forEach { keyword ->
            filters.add(
                Filters.or(
                    Filters.regex("titulo", keyword, "i"),
                    Filters.regex("descripcion", keyword, "i")
                )
            )
        }}

        params.category?.let { filters.add(Filters.eq("categorias", it))
        }
        params.maxPrice?.let { filters.add(Filters.lte("precio", it)) }

        params.minPrice?.let { filters.add(Filters.gte("precio", it)) }

        val finalFilter = if (filters.isNotEmpty()) {
            Filters.and(filters)
        } else {
            Document()
        }

        return collection.find(finalFilter)
            .sort(Sorts.ascending("_id"))
            .projection(projection)
            .skip((params.page - 1) * params.limit)
            .limit(params.limit)
            .toList()
    }

    fun save(usuario: ObjectId, evento: EventoInputDto): Evento {
        val newEvento = Evento(
            organizador = usuario,
            titulo = evento.titulo,
            descripcion = evento.descripcion,
            inicio = evento.inicio,
            duracion = evento.duracion,
            cupoMaximo = evento.cupoMaximo,
            cupoMinimo = evento.cupoMinimo,
            precio = evento.precio,
            categorias = evento.categorias
        )
        collection.insertOne(newEvento)
        collection.updateOne(
            Filters.eq("_id", newEvento.id),
            Updates.combine(
                Updates.set("inscriptos", emptyList<Document>()),
                Updates.set("esperas", emptyList<Document>())
            )
        )
        return newEvento
    }

    fun getFromId(id: ObjectId): Evento {
        return collection.find(Filters.eq("_id", id)).projection(projection).first()
            ?: throw NoSuchElementException("Evento con id $id no encontrado")
    }

    fun getOwnerFromId(id: ObjectId): ObjectId {
        val doc = collection.withDocumentClass(Document::class.java)
            .find(Filters.eq("_id", id))
            .projection(Projections.include("organizador"))
            .first()
            ?: throw NoSuchElementException("Evento con id $id no encontrado")

        return doc.getObjectId("organizador")
    }

    fun inscribirUsuario(eventoId: ObjectId, usuarioId: ObjectId): Inscripcion {
        val now = LocalDateTime.now()

        val confirmed = collection.updateOne(
            Filters.and(
                Filters.eq("_id", eventoId),
                Filters.not(Filters.elemMatch("inscriptos", Filters.eq("usuario", usuarioId))),
                Filters.not(Filters.elemMatch("esperas", Filters.eq("usuario", usuarioId))),
                Filters.expr(Document("\$lt", listOf("\$cantInscripciones", "\$cupoMaximo")))
            ),
            Updates.combine(
                Updates.inc("cantInscripciones", 1),
                Updates.push("inscriptos", Inscripcion.Confirmada(usuarioId, now, now))
            )
        )

        if (confirmed.modifiedCount > 0L) {
            return Inscripcion.Confirmada(usuarioId, now, now)
        } else {
            val result = collection.updateOne(
                Filters.and(
                    Filters.eq("_id", eventoId),
                    Filters.not(Filters.elemMatch("inscriptos", Filters.eq("usuario", usuarioId))),
                    Filters.not(Filters.elemMatch("esperas", Filters.eq("usuario", usuarioId))),
                ),
                Updates.combine(
                    Updates.inc("cantEspera", 1),
                    Updates.push("esperas", Inscripcion.Espera(usuarioId, now))
                )
            )
            if (result.matchedCount == 0L) {
                throw IllegalStateException("Usuario $usuarioId ya está inscripto en el evento $eventoId")
            }
            return Inscripcion.Espera(usuarioId, now)
        }
    }

    data class ListEsperas(
        val esperas: List<Inscripcion.Espera>
    )

    fun cancelarInscripcion(eventoId: ObjectId, usuarioId: ObjectId) {
        //Asume user is waitlisted
        val result = collection.updateOne(
            Filters.and(
                Filters.eq("_id", eventoId),
                Filters.elemMatch("esperas", Filters.eq("usuario", usuarioId))
            ),
            Updates.combine(
                Updates.pull("esperas", Document("usuario", usuarioId)),
                Updates.inc("cantEsperaCancelada", 1)
            )
        )

        if (result != null && result.modifiedCount > 0L) {
            return
        }

        // User wasn't waitlised, try and find him in inscriptos and promote first in waitlist if found
        val promoted = collection.withDocumentClass(ListEsperas::class.java).findOneAndUpdate(
            Filters.and(
                Filters.eq("_id", eventoId),
                Filters.elemMatch("inscriptos", Filters.eq("usuario", usuarioId))
            ),
            Updates.combine(
                Updates.pull("inscriptos", Document("usuario", usuarioId)),
                Updates.popFirst("esperas")
            ),
            FindOneAndUpdateOptions()
                .returnDocument(ReturnDocument.BEFORE)
                .projection(Projections.slice("esperas", 1))
        )

        // User wasn't found in inscriptos or esperas
        if (promoted == null) {
            throw NoSuchElementException("Usuario $usuarioId no está inscripto en el evento $eventoId")
        }

        // If there was no one in the waitlist, just decrease the count of inscriptos
        if (promoted.esperas.isEmpty()) {
            collection.updateOne(
                Filters.eq("_id", eventoId),
                Updates.inc("cantInscripciones", -1)
            )

        }
        // If there was someone in the waitlist, promote them
        else {
            collection.updateOne(
                Filters.eq("_id", eventoId),
                Updates.combine(
                    Updates.inc("cantEsperaExitosas", 1),
                    Updates.push("inscriptos", promoted.esperas[0].confirmar())
                )
            )
        }
    }

    fun deleteEvento(id: ObjectId) {
        collection.deleteOne(Filters.eq("_id", id))
    }

    fun getInscripcion(eventoId: ObjectId, usuarioId: ObjectId): Inscripcion {
        val inscripciones = collection.withDocumentClass(Inscripciones::class.java)
            .find(Filters.eq("_id", eventoId))
            .projection(
                Projections.fields(
                    Projections.elemMatch("inscriptos", Filters.eq("usuario", usuarioId)),
                    Projections.elemMatch("esperas", Filters.eq("usuario", usuarioId))
                )
            )
            .first() ?: throw NoSuchElementException("Evento con id $eventoId no encontrado")

        return inscripciones.inscriptos?.first()
            ?: inscripciones.esperas?.first()
            ?: throw NoSuchElementException("Usuario $usuarioId no está inscripto en el evento $eventoId")
    }

    @Suppress("UNCHECKED_CAST")
    fun batchGetInscripto(eventoId: ObjectId): Inscriptos {
        val inscriptos = collection.withDocumentClass(Document::class.java)
            .find(Filters.eq("_id", eventoId))
            .projection(Projections.include("inscriptos.usuario", "esperas.usuario"))
            .first() ?: throw NoSuchElementException("Evento con id $eventoId no encontrado")

        return Inscriptos(
            inscriptos = (inscriptos["inscriptos"] as List<Document>).map { it.getObjectId("usuario") },
            esperas = (inscriptos["esperas"] as List<Document>).map { it.getObjectId("usuario") }
        )
    }

    fun batchGetFromId(ids: Set<ObjectId>): Map<ObjectId, Evento> {
        return collection.find(Filters.`in`("_id", ids)).projection(projection)
            .into(HashSet()).associateBy { it.id }
    }
}