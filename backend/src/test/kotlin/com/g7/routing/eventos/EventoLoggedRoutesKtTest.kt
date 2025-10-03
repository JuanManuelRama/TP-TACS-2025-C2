package com.g7.routing.eventos

import com.g7.BaseMongoTest
import com.g7.addAuth
import com.g7.evento.EventoResponseDto
import com.g7.repo.EventoRepo
import com.g7.repo.UsuarioRepo
import com.g7.server.middleware.login.JwtConfig
import com.g7.usuario.Usuario
import com.g7.usuario.dto.toResponseDto
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestMethodOrder
import kotlin.test.Test
import kotlin.test.assertEquals

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class EventoLoggedRoutesKtTest: BaseMongoTest() {
    lateinit var eventoCreator: Usuario
    lateinit var eventoId: ObjectId
    lateinit var usuario2: Usuario
    lateinit var usuario3: Usuario
    lateinit var usuario4: Usuario

    @Test @Order(1)
    fun createEvent() = withTestApp {
        eventoCreator = UsuarioRepo.save(dataset.usuarios[0])
        val evento = dataset.eventos[0]
        client.post("/eventos") {
            addAuth(JwtConfig.generateToken(eventoCreator))
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(evento))
        }.apply {
            assertEquals(HttpStatusCode.Created, status, "Should return 201 Created")
            val returnedEvent = Json.decodeFromString<EventoResponseDto>(bodyAsText())
            eventoId = returnedEvent.id
            assertEquals(evento.titulo, returnedEvent.titulo, "Returned event title should match")
            assertEquals(eventoCreator.toResponseDto(), returnedEvent.organizador, "Returned event organizer should match" )
        }
    }

    @Test @Order(2)
    fun usuarioSubscribesToEvent() = withTestApp {
        usuario2 = UsuarioRepo.save(dataset.usuarios[1])

        client.post("/eventos/${eventoId}/inscriptos") {
            addAuth(JwtConfig.generateToken(usuario2))
        }.apply {
            assertEquals(HttpStatusCode.Created, status, "Should return 200 OK")
            val inscriptos = EventoRepo.batchGetInscripcion(eventoId)

            assertEquals(1, inscriptos.size, "Event should have one subscriber")
            assertEquals(usuario2.id, inscriptos[0].usuario, "Subscriber should match")
            assertEquals("CONFIRMACION", inscriptos[0].tipo, "Subscriber should match")
            assertEquals(1, EventoRepo.getFromId(eventoId).cantInscripciones, "Event should have one subscription" )
        }
    }

    @Test @Order(3)
    fun usuarioCannotSubscribeTwice() = withTestApp {
        client.post("/eventos/${eventoId}/inscriptos") {
            addAuth(JwtConfig.generateToken(usuario2))
        }.apply {
            assertEquals(HttpStatusCode.Conflict, status, "Should return 409 Conflict")
            val inscriptos = EventoRepo.batchGetInscripcion(eventoId)
            assertEquals(1, inscriptos.size, "Event should still have one subscriber")
        }
    }

    @Test @Order(4)
    fun usuarioGetsWaitlisted() = withTestApp {
        usuario3 = UsuarioRepo.save(dataset.usuarios[2])

        client.post("/eventos/${eventoId}/inscriptos") {
            addAuth(JwtConfig.generateToken(usuario3))
        }.apply {
            assertEquals(HttpStatusCode.Created, status, "Second subscriber should be created")
            val inscriptos = EventoRepo.batchGetInscripcion(eventoId)

            assertEquals(2, inscriptos.size, "Event should have two subscribers")
            assertEquals(usuario3.id, inscriptos[1].usuario, "Second subscriber should match")
            assertEquals("ESPERA", inscriptos[1].tipo, "Second subscriber should be on waitlist")
            assertEquals(1, EventoRepo.getFromId(eventoId).cantEspera, "Evento should have 1 parson waitlisted" )
        }
    }

    @Test @Order(5)
    fun waitlistRespectsOrder() = withTestApp {
        usuario4 = UsuarioRepo.save(dataset.usuarios[3])

        client.post("/eventos/${eventoId}/inscriptos") {
            addAuth(JwtConfig.generateToken(usuario4))
        }
        client.delete("/eventos/${eventoId}/inscriptos") {
            addAuth(JwtConfig.generateToken(usuario2))
        }.apply {
            assertEquals(HttpStatusCode.NoContent, status, "Waitlist promotion should return 204 No Content")
            val inscriptos = EventoRepo.batchGetInscripcion(eventoId)

            assertEquals(2, inscriptos.size, "Event should have two subscribers")
            assertEquals(usuario3.id, inscriptos[0].usuario, "First waitlisted user should be promoted to subscriber")
            assertEquals("CONFIRMACION", inscriptos[0].tipo, "First waitlisted user should be promoted to subscriber")
            assertEquals("ESPERA", inscriptos[1].tipo, "Second waitlisted user should still be on waitlist")
            assertEquals(1, EventoRepo.getFromId(eventoId).cantInscripciones, "Evento should have 1 confirmed subscription" )
            assertEquals(1, EventoRepo.getFromId(eventoId).cantEsperaExitosas, "Evento should have 1 successful waitlist" )
        }
    }

    @Test @Order(6)
    fun userCannotUnsubscribeIfNotSubscribed() = withTestApp {
        client.delete("/eventos/${eventoId}/inscriptos") {
            addAuth(JwtConfig.generateToken(usuario2))
        }.apply {
            assertEquals(HttpStatusCode.NotFound, status, "Should return 404 Not Found")
            val inscriptos = EventoRepo.batchGetInscripcion(eventoId)
            assertEquals(2, inscriptos.size, "Event should still have two subscribers")
        }
    }

    @Test @Order(7)
    fun eventoHasCorretStats() = withTestApp {
        client.get("/eventos/${eventoId}/estadisticas") {
        }.apply {
            assertEquals(HttpStatusCode.OK, status, "Should return 200 OK")
            val stats = Json.decodeFromString<Map<String, Float?>>(bodyAsText())
            assertEquals(100.0f, stats["procentajeLleno"], "Event should be 100% full")
            assertEquals(50.0f, stats["porcentajeExito"], "Event should have 50% waitlist success rate")
            assertEquals(0.0f, stats["porcentajeCancelacion"], "Event should have 0% waitlist cancellation rate")
        }
    }

    @Test @Order(8)
    fun nonOwnerCannotRemoveSubscriber() = withTestApp {
        client.delete("/eventos/${eventoId}/inscriptos/${usuario4.id}") {
            addAuth(JwtConfig.generateToken(usuario3))
        }.apply {
            assertEquals(HttpStatusCode.Forbidden, status, "Non-owner should not be able to remove subscriber")
            val inscriptos = EventoRepo.batchGetInscripcion(eventoId)
            assertEquals(2, inscriptos.size, "Event should still have two subscribers")
        }
    }

    @Test @Order(9)
    fun ownerCanRemoveSubscriber() = withTestApp {
        client.delete("/eventos/${eventoId}/inscriptos/${usuario3.id}") {
            addAuth(JwtConfig.generateToken(eventoCreator))
        }.apply {
            assertEquals(HttpStatusCode.NoContent, status, "Owner should be able to remove subscriber")
            val inscriptos = EventoRepo.batchGetInscripcion(eventoId)
            assertEquals(1, inscriptos.size, "Event should have one subscriber left")
            assertEquals(usuario4.id, inscriptos[0].usuario, "Remaining subscriber should be the waitlisted user")
            assertEquals("CONFIRMACION", inscriptos[0].tipo, "Remaining subscriber should be confirmed")
        }
    }


}