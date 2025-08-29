package com.g7.server

import com.g7.evento.EventoDto
import com.g7.evento.toDomain
import com.g7.evento.toDto
import com.g7.repo.EventoRepository
import com.g7.repo.UsuarioRepository
import com.g7.usuario.toDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import java.util.UUID

fun Route.eventoRoutes() {

    get("/eventos/") {
        call.respond(HttpStatusCode.OK, EventoRepository.getEventos().map { it.toDto() })
    }

    get("/eventos/{id}") {
        val id = call.requireUuidParam("id") ?: return@get
        EventoRepository.findById(id)
            .onSuccess { evento ->
                call.respond(HttpStatusCode.OK, evento.toDto())
            }
            .onFailure {
                call.respond(HttpStatusCode.NotFound, "${it.message}")
            }
    }

    post("/eventos") {
        val eventoDto = call.receive<EventoDto>().copy(id = UUID.randomUUID())
        //TODO: generar una id en serio
        eventoDto.toDomain()
            .onSuccess {
                EventoRepository.saveEvento(it)
                call.respond(HttpStatusCode.Created, eventoDto)
            }
            .onFailure {
                call.respond(HttpStatusCode.BadRequest, "Error al inscribir el evento: ${it.message}")
            }
    }

    get("/eventos/{id}/inscriptos") {
        val id = call.requireUuidParam("id")?: return@get

        EventoRepository.findById(id)
            .onSuccess { evento ->
                call.respond(HttpStatusCode.OK,
                    evento.inscriptos.map { it.toDto() } + evento.enEspera.map { it.toDto() })
            }
            .onFailure {
                call.respond(HttpStatusCode.NotFound, "${it.message}")
            }
    }
    //usuarioId debería venir del contexto (jwt)
    post("eventos/{id}/inscriptos/{usuarioId}") {
        val id = call.requireUuidParam("id")?: return@post
        val usuarioId = call.requireUuidParam("usuarioId")?: return@post
        EventoRepository.findById(id)
            .onSuccess { evento ->
                UsuarioRepository.getUsuarioFromId(usuarioId)
                    .onSuccess { usuario ->
                        evento.inscribir(usuario)
                            .onSuccess { call.respond(HttpStatusCode.OK, it.toDto())}
                            .onFailure {
                                call.respond(HttpStatusCode.BadRequest, it.message ?: "Unkown error")
                            }
                    }
                    .onFailure {
                        call.respond(HttpStatusCode.NotFound, it.message ?: "Unknown error") }
            }
            .onFailure { call.respond(HttpStatusCode.NotFound, it.message ?: "Unknown error") }
    }

    delete ("eventos/{id}/inscriptos/{usuarioId}") {
        val id = call.requireUuidParam("id")?: return@delete
        val usuarioId = call.requireUuidParam("usuarioId")?: return@delete
        EventoRepository.findById(id)
            .onSuccess { evento ->
                UsuarioRepository.getUsuarioFromId(usuarioId)
                .onSuccess { usuario ->
                    evento.cancelar(usuario)
                        .onSuccess { call.respond(HttpStatusCode.OK) }
                        .onFailure { call.respond(HttpStatusCode.BadRequest, it.message ?: "Unkown error") }
                }
                .onFailure { call.respond(HttpStatusCode.NotFound, it.message ?: "Unknown error") }
            }
        .onFailure { call.respond(HttpStatusCode.NotFound, it.message ?: "Unknown error") }
    }

}