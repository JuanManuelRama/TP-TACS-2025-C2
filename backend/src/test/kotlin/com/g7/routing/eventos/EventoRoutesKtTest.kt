package com.g7.routing.eventos

import com.g7.BaseMongoTest
import com.g7.evento.EventoResponseDto
import com.g7.repo.EventoRepo
import com.g7.repo.UsuarioRepo
import com.g7.usuario.dto.toResponseDto
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId
import kotlin.test.Test
import kotlin.test.assertEquals

class EventoRoutesKtTest: BaseMongoTest() {

    @Test
    fun returnsCorrectEvent() = withTestApp {
        val inputUsuario = dataset.usuarios[0]
        val usuario = UsuarioRepo.save(inputUsuario)
        val inputEvento = dataset.eventos[0]
        val evento = EventoRepo.save(usuario.id, inputEvento)
        client.get("/eventos/${evento.id}").apply {
            assertEquals(HttpStatusCode.OK, status, "Should return 200 OK")

            val returnedEvent = Json.decodeFromString<EventoResponseDto>(bodyAsText())
            assertEquals(evento.id, returnedEvent.id, "Returned event ID should match")
            assertEquals(evento.titulo, returnedEvent.titulo, "Returned event title should match")
            assertEquals(usuario.toResponseDto(), returnedEvent.organizador, "Returned event organizer should match" )
        }
    }

    @Test
    fun respondsNotFoundForNonExistentEvent() = withTestApp {
        client.get("/eventos/${ObjectId()}").apply {
            assertEquals(HttpStatusCode.NotFound, status, "Should return 404 Not Found")
        }
    }
}