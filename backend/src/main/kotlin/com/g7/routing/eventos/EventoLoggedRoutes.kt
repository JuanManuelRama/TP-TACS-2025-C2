package com.g7.routing.eventos

import com.g7.evento.EventoDto
import com.g7.evento.register
import com.g7.evento.toDto
import com.g7.repo.EventoRepository
import com.g7.server.fetchEvento
import com.g7.server.fetchUsuario
import com.g7.server.middleware.login.loggedUser
import com.g7.server.requireUuidParam
import com.g7.server.respondError
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Route.eventoLoggedRoutes() {
    post {
        val userId = call.loggedUser()?.id ?: return@post call
            .respondError(HttpStatusCode.Unauthorized, "No se pudo obtener el usuario logueado")
        val eventoDto = call.receive<EventoDto>()
        eventoDto.register(userId)
            .onSuccess { evento ->
                EventoRepository.saveEvento(evento)
                call.respond(HttpStatusCode.Created, evento.toDto())
            }
            .onFailure { call.respondError(HttpStatusCode.BadRequest, it.message) }
    }

    get("/{id}/inscriptos") {
        val id = call.requireUuidParam("id")?: return@get
        val user = call.loggedUser() ?: return@get call.respond(HttpStatusCode.Unauthorized)
        val evento = call.fetchEvento(id) ?: return@get

        if (user.id != evento.organizador.id) {
            return@get call.respondError(HttpStatusCode.Unauthorized, "Solo el organizador puede verlo")
        }

        val inscriptos = evento.inscriptos.map { it.toDto() } + evento.enEspera.map { it.toDto() }
        call.respond(HttpStatusCode.OK, inscriptos)

    }

    post("/{id}/inscriptos") {
        val id = call.requireUuidParam("id") ?: return@post
        val userLog = call.loggedUser() ?: return@post call.respond(HttpStatusCode.Unauthorized)

        val evento = call.fetchEvento(id) ?: return@post
        val usuario = call.fetchUsuario(userLog.id) ?: return@post

        if (usuario.id == evento.organizador.id) {
            return@post call.respondError(HttpStatusCode.Forbidden,
                "El organizador no puede inscribirse en su propio evento")
        }

        evento.inscribir(usuario)
            .onSuccess { call.respond(HttpStatusCode.OK, it.toDto()) }
            .onFailure { call.respondError(HttpStatusCode.BadRequest, it.message) }
    }

    delete("/{id}/inscriptos") {
        val id = call.requireUuidParam("id") ?: return@delete
        val user = call.loggedUser() ?: return@delete call.respond(HttpStatusCode.Unauthorized)

        val evento = call.fetchEvento(id) ?: return@delete
        val usuario = call.fetchUsuario(user.id) ?: return@delete

        evento.cancelar(usuario)
            .onSuccess { call.respond(HttpStatusCode.OK) }
            .onFailure { call.respondError(HttpStatusCode.BadRequest, it.message) }
    }

    get("/{id}/inscriptos/{userId}") {
        val id = call.requireUuidParam("id") ?: return@get
        val userId = call.requireUuidParam("userId") ?: return@get
        val loggedUser = call.loggedUser() ?: return@get call.respond(HttpStatusCode.Unauthorized)

        val evento = call.fetchEvento(id) ?: return@get

        if (loggedUser.id != evento.organizador.id && loggedUser.id != userId)  {
            return@get call.respondError(HttpStatusCode.Forbidden,
                "solo el organizador o si mismo puede ver la inscripción")
        }
        val inscripcion = evento.inscriptos.find { it.usuario.id == userId }
            ?: evento.enEspera.find { it.usuario.id == userId }
            ?: return@get call.respondError(HttpStatusCode.NotFound, "inscripcion no encontrada")
        return@get call.respond(inscripcion.toDto())
    }

    delete("/{id}/inscriptos/{userId}") {
        val id = call.requireUuidParam("id") ?: return@delete
        val user = call.loggedUser() ?: return@delete call.respond(HttpStatusCode.Unauthorized)

        val evento = call.fetchEvento(id) ?: return@delete
        var usuario = call.fetchUsuario(user.id) ?: return@delete

        if (usuario.id != evento.organizador.id) {
            return@delete call.respondError(HttpStatusCode.Forbidden,
                "solo el organizador puede echar a alguien")
        }

        val userId = call.requireUuidParam("userId") ?: return@delete
        usuario = call.fetchUsuario(userId) ?: return@delete

        evento.cancelar(usuario)
            .onSuccess { call.respond(HttpStatusCode.OK) }
            .onFailure { call.respondError(HttpStatusCode.BadRequest, it.message) }
    }
}