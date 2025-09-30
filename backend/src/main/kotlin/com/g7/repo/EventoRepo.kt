package com.g7.repo

import com.g7.evento.Evento
import com.g7.evento.EventoInputDto
import com.g7.evento.FiltroEvento
import com.g7.evento.Inscripcion
import com.g7.evento.InscripcionDto
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Projections
import com.mongodb.client.model.Updates
import org.bson.Document
import org.bson.types.ObjectId
import java.lang.IllegalStateException
import java.time.LocalDateTime

data class Algo(
    val inscriptos: MutableList<Inscripcion.Confirmada>,
    val esperas: MutableList<Inscripcion.Espera>,
    var cantInscripciones: Long,
    var cantEspera: Int,
    var cantEsperaExitosas: Int,
    var cantEsperaCancelada: Int
)

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
            cupoMinimio = evento.cupoMinimio,
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

        val updateResult = collection.updateOne(
            Filters.eq("_id", eventoId),
            inscribirUsuarioPipeline(usuarioId, now)
        )

        if (updateResult.modifiedCount == 0L) {
            throw IllegalStateException("Usuario $usuarioId ya está registrado en evento $eventoId")
        }

        val inscripcionDoc = collection.withDocumentClass(Document::class.java)
            .find(Filters.eq("_id", eventoId))
            .projection(
                Projections.fields(
                    Projections.elemMatch("inscriptos", Filters.eq("usuario", usuarioId)),
                    Projections.elemMatch("esperas", Filters.eq("usuario", usuarioId))
                )
            )
            .first()

        val confirmado = (inscripcionDoc?.get("inscriptos") as? List<*>)?.firstOrNull()
        val enEspera = (inscripcionDoc?.get("esperas") as? List<*>)?.firstOrNull()

        return when {
            confirmado != null -> Inscripcion.Confirmada(usuarioId, now, now)
            enEspera != null -> Inscripcion.Espera(usuarioId, now)
            else -> throw RuntimeException("No se pudo inscribir al usuario $usuarioId en el evento $eventoId")
        }
    }

    fun cancelarInscripcion(eventoId: ObjectId, usuarioId: ObjectId) {
        val ses = MongoProvider.client.startSession()
        ses.startTransaction()
        val event = collection.withDocumentClass(Algo::class.java)
            .find(Filters.eq("_id", eventoId))
            .projection(
                Projections.include ("inscriptos", "esperas", "cantInscripciones",
                    "cantEspera", "cantEsperaExitosas", "cantEsperaCancelada"
                )
            ).first() ?: throw NoSuchElementException("Evento with id: $eventoId not found")

        val removedFromInscriptos = event.inscriptos.removeIf { it.usuario == usuarioId }
        val removedFromEsperas = event.esperas.removeIf { it.usuario == usuarioId }

        if (!removedFromInscriptos && !removedFromEsperas) {
            throw IllegalStateException("Usuario $usuarioId no está inscripto en el evento $eventoId")
        }

        if (removedFromInscriptos && event.esperas.isNotEmpty()) {
            val promoted = event.esperas.removeAt(0).confirmar()
            event.inscriptos.add(promoted)
            event.cantEsperaExitosas++
        }
        event.cantInscripciones = event.inscriptos.size.toLong()
        if (removedFromEsperas) {
            event.cantEsperaCancelada++
        }

        collection.updateOne(
            Filters.eq("_id", eventoId),
            Updates.combine(
                Updates.set("inscriptos", event.inscriptos),
                Updates.set("esperas", event.esperas),
                Updates.set("cantEspera", event.cantEspera),
                Updates.set("cantInscripciones", event.cantInscripciones),
                Updates.set("cantEsperaExitosas", event.cantEsperaExitosas),
                Updates.set("cantEsperaCancelada", event.cantEsperaCancelada)
            )
        )
        ses.commitTransaction()
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

        if (inscriptos.esperas == null || inscriptos.inscriptos == null) {
            throw RuntimeException("No deberían ser null las listas de inscriptos o esperas")
        }

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

        return when {
            !inscripciones.inscriptos.isNullOrEmpty() -> inscripciones.inscriptos[0]
            !inscripciones.esperas.isNullOrEmpty() -> inscripciones.esperas[0]
            else -> throw NoSuchElementException("Usuario $usuarioId no está inscripto en el evento $eventoId")
        }
    }
}