package com.g7.routing.usuarios.eventos

import com.g7.application.middleware.login.loggedUser
import com.g7.evento.toDto
import com.g7.repo.EventoRepo
import com.g7.repo.UsuarioRepo
import com.g7.service.EventoService
import com.g7.usuario.toDto
import com.g7.usuario.toResponseDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.usuariosEventos() {
    authenticate ("auth-jwt") {
        get {
            val id = call.loggedUser().id
            val eventos = UsuarioRepo.getEventos(id)
            val ids = (eventos.eventosCreados ?: emptyList()) +
                    (eventos.eventosConfirmados ?: emptyList()) +
                    (eventos.eventosEnEspera ?: emptyList())

            val eventosMap = EventoService.getEventos(ids.toSet())

            call.respond(HttpStatusCode.OK, eventos.toDto(eventosMap))
        }
    }
}