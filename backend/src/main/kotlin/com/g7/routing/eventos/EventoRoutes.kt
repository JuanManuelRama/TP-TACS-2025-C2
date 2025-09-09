package com.g7.routing.eventos

import com.g7.evento.toDto
import com.g7.repo.EventoRepository
import com.g7.repo.UsuarioRepository
import com.g7.server.fetchEvento
import com.g7.server.requireUuidParam
import com.g7.server.respondError
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get

fun Route.eventoRoutes() {
    get {
        call.respond(HttpStatusCode.OK, EventoRepository.getEventos().map { it.toDto() })
    }

    get("/{id}") {
        val id = call.requireUuidParam("id") ?: return@get
        val evento = call.fetchEvento(id) ?: return@get
        call.respond(HttpStatusCode.OK, evento.toDto())
    }
    authenticate("auth-jwt") {
        eventoLoggedRoutes()
    }

}
