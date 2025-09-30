package com.g7.routing.eventos

import com.g7.BaseMongoTest
import com.g7.BaseMongoTest.Companion.dataset
import com.g7.addAuth
import com.g7.evento.EventoResponseDto
import com.g7.repo.UsuarioRepo
import com.g7.server.middleware.login.JwtConfig
import com.g7.setupTestApplication
import com.g7.usuario.dto.toResponseDto
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class EventoLoggedRoutesKtTest: BaseMongoTest() {

    @Test
    fun createEvent() = withTestApp {
        val inputUsuario = dataset.usuarios[0]
        val usuario = UsuarioRepo.save(inputUsuario)
        client.post("/eventos") {
            addAuth(JwtConfig.generateToken(usuario))
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(dataset.eventos[0]))
        }.apply {
            assertEquals(HttpStatusCode.Created, status, "Should return 201 Created")
            val returnedEvent = Json.decodeFromString<EventoResponseDto>(bodyAsText())
            assertEquals(dataset.eventos[0].titulo, returnedEvent.titulo, "Returned event title should match")
            assertEquals(usuario.toResponseDto(), returnedEvent.organizador, "Returned event organizer should match" )
        }
    }

}