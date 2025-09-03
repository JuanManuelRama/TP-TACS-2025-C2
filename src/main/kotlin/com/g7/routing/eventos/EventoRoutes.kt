package com.g7.routing.eventos

import com.g7.evento.EventoDto
import com.g7.evento.toDomain
import com.g7.evento.toDto
import com.g7.repo.EventoRepository
import com.g7.repo.UsuarioRepository
import com.g7.server.loggedUser
import com.g7.server.requireUuidParam
import com.g7.server.respondError
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import java.util.UUID

fun Route.eventoRoutes() {

    get {
        call.respond(HttpStatusCode.OK, EventoRepository.getEventos().map { it.toDto() })
    }

    get("/{id}") {
        val id = call.requireUuidParam("id") ?: return@get
        EventoRepository.findById(id)
            .onSuccess { evento ->
                call.respond(HttpStatusCode.OK, evento.toDto())
            }
            .onFailure {
                call.respondError(HttpStatusCode.NotFound, "${it.message}")
            }
    }

    post {
        val eventoDto = call.receive<EventoDto>()
            .copy(id = UUID.randomUUID())
            .copy(organizador = call.loggedUser()?.id ?: return@post call.respondError(
                HttpStatusCode.Unauthorized,
                "No se pudo obtener el usuario logueado"
            ))
        //TODO: generar una id en serio
        eventoDto.toDomain()
            .onSuccess {
                EventoRepository.saveEvento(it)
                call.respond(HttpStatusCode.Created, eventoDto)
            }
            .onFailure {
                call.respondError(HttpStatusCode.BadRequest, "Error al inscribir el evento: ${it.message}")
            }
    }

    get("/{id}/inscriptos") {
        val id = call.requireUuidParam("id")?: return@get

        EventoRepository.findById(id)
            .onSuccess { evento ->
                call.respond(HttpStatusCode.OK,
                    evento.inscriptos.map { it.toDto() } + evento.enEspera.map { it.toDto() })
            }
            .onFailure {
                call.respondError(HttpStatusCode.NotFound, "${it.message}")
            }
    }

    post("/{id}/inscriptos}") {
        val id = call.requireUuidParam("id")?: return@post
        val user = call.loggedUser() ?: return@post call.respond(HttpStatusCode.Unauthorized)
        val evento = EventoRepository.findById(id).getOrElse {
            return@post call.respond(HttpStatusCode.NotFound, it.message ?: "Unknown error")
        }
        val usuario = UsuarioRepository.getUsuarioFromId(user.id).getOrElse {
            return@post call.respond(HttpStatusCode.NotFound, it.message ?: "Unknown error")
        }
        evento.inscribir(usuario)
            .onSuccess { call.respond(HttpStatusCode.OK, it.toDto())}
            .onFailure {
                call.respondError(HttpStatusCode.BadRequest, it.message ?: "Unkown error")
            }
    }

    delete ("/{id}/inscriptos/{usuarioId}") {
        val id = call.requireUuidParam("id")?: return@delete
        val usuarioId = call.requireUuidParam("usuarioId")?: return@delete
        EventoRepository.findById(id)
            .onSuccess { evento ->
                UsuarioRepository.getUsuarioFromId(usuarioId)
                .onSuccess { usuario ->
                    evento.cancelar(usuario)
                        .onSuccess { call.respond(HttpStatusCode.OK) }
                        .onFailure { call.respondError(HttpStatusCode.BadRequest, it.message ?: "Unkown error") }
                }
                .onFailure { call.respondError(HttpStatusCode.NotFound, it.message ?: "Unknown error") }
            }
        .onFailure { call.respondError(HttpStatusCode.NotFound, it.message ?: "Unknown error") }
    }

}