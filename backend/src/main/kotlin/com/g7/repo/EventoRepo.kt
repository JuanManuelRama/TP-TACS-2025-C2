package com.g7.repo

import com.g7.evento.Evento
import com.g7.evento.EventoInputDto
import com.g7.evento.FiltroEvento
import com.g7.evento.Inscripcion
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.*
import org.bson.Document
import org.bson.types.ObjectId
import java.time.LocalDateTime

data class Inscripciones (
    val inscriptos: List<Inscripcion.Confirmada> = emptyList(),
    val esperas: List<Inscripcion.Espera> = emptyList()
)

object EventoRepo {
    private lateinit var collection: MongoCollection<Evento>
    private val projection = Projections.exclude("inscriptos", "esperas")

    fun init() {
        collection = MongoProvider.eventoCollection
    }

    fun getEventos(): List<Evento> {
        return collection.find().projection(projection).toList()
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

    fun getEventosFiltrado(filtro: FiltroEvento): List<Evento> {
        TODO("Not yet implemented")
    }

    fun deleteEvento(id: ObjectId) {
        collection.deleteOne(Filters.eq("_id", id))
    }

    fun batchGetInscripcion(eventoId: ObjectId): List<Inscripcion> {
        val inscriptos = collection.withDocumentClass(Inscripciones::class.java)
            .find(Filters.eq("_id", eventoId))
            .projection(Projections.include("inscriptos", "esperas"))
            .first() ?: throw NoSuchElementException("Evento con id $eventoId no encontrado")

        return inscriptos.inscriptos + inscriptos.esperas
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

        return inscripciones.inscriptos.firstOrNull()
            ?: inscripciones.esperas.firstOrNull()
            ?: throw NoSuchElementException("Usuario $usuarioId no está inscripto en el evento $eventoId")
    }
}