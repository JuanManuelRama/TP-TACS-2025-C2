package com.g7.routing.eventos

import com.g7.BaseMongoTest
import com.g7.addAuth
import com.g7.evento.EventoResponseDto
import com.g7.evento.toDto
import com.g7.repo.EventoRepo
import com.g7.repo.UsuarioRepo
import com.g7.usuario.Usuario
import com.g7.usuario.toResponseDto
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class PostTest: BaseMongoTest() {
    lateinit var creator: Usuario

    override fun populateTestData() {
        creator = UsuarioRepo.save(dataset.usuarios[0])
    }

    @Test
    fun createEvento() = withTestApp {
        val evento = dataset.eventos[0]
        client.post("/eventos") {
            addAuth(creator)
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(evento))
        }.apply {
            assertEquals(io.ktor.http.HttpStatusCode.Created, status)
            val createdEvento = Json.decodeFromString<EventoResponseDto>(bodyAsText())
            assertEquals(createdEvento.titulo, evento.titulo, "Returned title should match input")
            val creatorDto = creator.toResponseDto()
            assertEquals(createdEvento.organizador,creatorDto, "Creator should match")
            assertEquals(createdEvento, EventoRepo.getFromId(createdEvento.id).toDto(creatorDto),
                "Created evento should match Db")
        }
    }

    @Test
    fun createEventoInvalidData() = withTestApp {
        client.post("/eventos") {
            addAuth(creator)
            contentType(ContentType.Application.Json)
            setBody("{}")
        }.apply {
            assertEquals(io.ktor.http.HttpStatusCode.BadRequest, status)
            assertEquals(0, EventoRepo.getEventos().size, "No evento gets created")
        }
    }

    @Test
    fun createEventoNoAuth() = withTestApp {
        val evento = dataset.eventos[0]
        client.post("/eventos") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(evento))
        }.apply {
            assertEquals(io.ktor.http.HttpStatusCode.Unauthorized, status)
            assertEquals(0, EventoRepo.getEventos().size, "No evento gets created")
        }
    }
}