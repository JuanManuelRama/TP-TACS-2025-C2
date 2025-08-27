package com.g7.server

import com.g7.evento.EventoDto
import com.g7.evento.toDomain
import com.g7.evento.toDto
import com.g7.repo.EventoRepository
import com.g7.repo.UsuarioRepository
import com.g7.usuario.UsuarioDto
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
        get("/eventos/{id}") {
            val idParam = call.parameters["id"] ?: return@get call
                .respond(HttpStatusCode.BadRequest, "Missing id")
            val id = try {
                UUID.fromString(idParam)
            } catch (_: IllegalArgumentException) {
                return@get call.respond(HttpStatusCode.BadRequest, "Invalid Id format")
            }
            val evento = EventoRepository.getEventos().find { it.id == id }
                ?: return@get call.respond(HttpStatusCode.NotFound, "Evento no encontrado")
            call.respond(HttpStatusCode.OK, evento.toDto())
        }
        post("/usuario") {
            val usuarioDto = call.receive<UsuarioDto>().copy(id = UUID.randomUUID())
            usuarioDto.toDomain()
                .onSuccess {
                    UsuarioRepository.save(it)
                    call.respond(HttpStatusCode.Created, usuarioDto)
                }
                .onFailure {
                    call.respond(HttpStatusCode.BadRequest, "Erro al registrar usuario: ${it.message}")
                }
        }
        /**
        * La id de usuario debería entrar por contexto de la request (jwt),
         * por ahora se pasa en el recurso
        * */
        post("/evento") {
            val eventoDto = call.receive<EventoDto>().copy(id = UUID.randomUUID())
            //TODO: generar una id en serio
            eventoDto.toDomain()
                .onSuccess {
                    EventoRepository.saveEvento(it)
                    call.respond(HttpStatusCode.Created, eventoDto)
                }
                .onFailure {
                    call.respond(HttpStatusCode.BadRequest, "Erro al registrar el evento: ${it.message}")
                }
        }
    }
}
