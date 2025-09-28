package com.g7.repo

import com.mongodb.client.model.Updates
import org.bson.Document
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import java.time.LocalDateTime

fun inscribirUsuarioPipeline(usuarioId: ObjectId, now: LocalDateTime): List<Document> =
    listOf(
        Document("\$set", Document().apply {
            // Compute if user is already registered (inline)
            val usuarioYaRegistrado = Document("\$or", listOf(
                Document("\$in", listOf(
                    usuarioId,
                    Document("\$map", Document().apply {
                        put("input", "\$inscriptos")
                        put("as", "inscripcion")
                        put("in", "$\$inscripcion.usuario")
                    })
                )),
                Document("\$in", listOf(
                    usuarioId,
                    Document("\$map", Document().apply {
                        put("input", "\$esperas")
                        put("as", "espera")
                        put("in", "$\$espera.usuario")
                    })
                ))
            ))

            // Only increment if user is NOT already registered AND has space
            put("cantInscripciones", Document("\$cond", Document().apply {
                put("if", Document("\$and", listOf(
                    Document("\$not", usuarioYaRegistrado),
                    Document("\$lt", listOf("\$cantInscripciones", "\$cupoMaximo"))
                )))
                put("then", Document("\$add", listOf("\$cantInscripciones", 1)))
                put("else", "\$cantInscripciones")
            }))

            // Add to inscriptos if user is NOT registered AND has space
            put("inscriptos", Document("\$cond", Document().apply {
                put("if", Document("\$and", listOf(
                    Document("\$not", usuarioYaRegistrado),
                    Document("\$lt", listOf("\$cantInscripciones", "\$cupoMaximo"))
                )))
                put("then", Document("\$concatArrays", listOf(
                    "\$inscriptos",
                    listOf(createInscripcionDoc(usuarioId, now, now))
                )))
                put("else", "\$inscriptos")
            }))

            // Add to esperas if user is NOT registered AND no space
            put("esperas", Document("\$cond", Document().apply {
                put("if", Document("\$and", listOf(
                    Document("\$not", usuarioYaRegistrado),
                    Document("\$gte", listOf("\$cantInscripciones", "\$cupoMaximo"))
                )))
                put("then", Document("\$concatArrays", listOf(
                    "\$esperas",
                    listOf(createInscripcionDoc(usuarioId, now, null))
                )))
                put("else", "\$esperas")
            }))


            put("cantEspera", Document("\$cond", Document().apply {
                put("if", Document("\$and", listOf(
                    Document("\$not", usuarioYaRegistrado),
                    Document("\$gte", listOf("\$cantInscripciones", "\$cupoMaximo"))
                )))
                put("then", Document("\$add", listOf("\$cantEspera", 1)))
                put("else", "\$cantEspera")
            }))
        })
    )

private fun createInscripcionDoc(
    usuarioId: ObjectId,
    horaInscripcion: LocalDateTime,
    horaConfirmacion: LocalDateTime?
) = Document().apply {
    put("usuario", usuarioId)
    put("horaInscripcion", horaInscripcion)
    put("horaConfirmacion", horaConfirmacion)
}

fun inscripcionWithUsuarioPipeline(eventoId: ObjectId, userId: ObjectId): List<Document> =
 listOf(
    Document("\$match", Document("_id", eventoId)),
    Document("\$project", Document("inscripcion", Document(
        "\$filter", Document()
            .append("input", Document("\$concatArrays", listOf("\$inscriptos", "\$esperas")))
            .append("as", "i")
            .append("cond", Document("\$eq", listOf("\$i.usuario", userId)))
    ))),
    Document("\$unwind", "\$inscripcion"),
    Document("\$lookup", Document()
        .append("from", "usuarios")
        .append("localField", "inscripcion.usuario")
        .append("foreignField", "_id")
        .append("as", "inscripcion.usuario")
    ),
    Document("\$unwind", "\$inscripcion.usuario"),
    Document("\$replaceRoot", Document("newRoot", "\$inscripcion"))
)
