package com.g7.routing.eventos.id

import com.g7.BaseMongoTest
import com.g7.evento.Evento
import com.g7.evento.EventoResponseDto
import com.g7.repo.EventoRepo
import com.g7.repo.UsuarioRepo
import com.g7.usuario.Usuario
import com.g7.usuario.toResponseDto
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class GetTest: BaseMongoTest() {
    lateinit var user: Usuario
    lateinit var evento1: Evento
    lateinit var evento2: Evento

    override fun populateTestData() {
        user = UsuarioRepo.save(dataset.usuarios[0])
        evento1 = EventoRepo.save(user.id, dataset.eventos[0])
        evento2 = EventoRepo.save(user.id, dataset.eventos[1])
    }

    @Test
    fun getEventoById() = withTestApp {
        client.get("/eventos/${evento1.id}").apply {
            assertEquals(io.ktor.http.HttpStatusCode.OK, status)
            val fetchedEvento = Json.decodeFromString<EventoResponseDto>(bodyAsText())
            assertEquals(evento1.titulo, fetchedEvento.titulo, "Fetched title should match")
            assertEquals(user.toResponseDto(), fetchedEvento.organizador, "Organizer should match")
        }
    }
}