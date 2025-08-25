package com.g7.server

import com.g7.evento.EventoDto
import com.g7.evento.toDomain
import com.g7.evento.toDto
import com.g7.repo.EventoRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import io.ktor.serialization.kotlinx.json.*
import java.util.UUID

fun Application.configureRouting() {
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
        )
    }

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/evento/{id}") {
            val idParam = call.parameters["id"] ?: return@get call
                .respond(HttpStatusCode.BadRequest, "Missing id")
            val id = try {
                UUID.fromString(idParam)
            } catch (e: IllegalArgumentException) {
                return@get call.respond(HttpStatusCode.BadRequest, "Invalid UUID format")
            }
            val evento = EventoRepository.getEventos().find { it.id == id }
                ?: return@get call.respond(HttpStatusCode.NotFound, "Evento no encontrado")
            call.respond(HttpStatusCode.OK, evento.toDto())
        }

        post("/nuevo-evento") {
            val evento = call.receive<EventoDto>().toDomain() // Ktor maneja la excepción solo
            EventoRepository.saveEvento(evento)
            call.respond(HttpStatusCode.Created, "Evento registrado: ${evento.id}")
        }
    }
}
