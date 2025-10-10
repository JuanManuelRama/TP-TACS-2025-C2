package com.g7.routing.eventos

import com.g7.evento.EventoInputDto
import com.g7.evento.toDto
import com.g7.repo.EventoRepo
import com.g7.repo.UsuarioRepo
import com.g7.routing.eventos.id.eventosId
import com.g7.application.middleware.login.loggedUser
import com.g7.usuario.toResponseDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.eventos() {
    get {
        val eventos = EventoRepo.getEventos()
        val maps = UsuarioRepo.batchGetFromId(eventos.map { it.organizador }.toSet())
        val dtos = eventos.map { evento -> evento.toDto(maps[evento.organizador]!!.toResponseDto()) }
        call.respond(HttpStatusCode.OK, dtos)
    }
    authenticate("auth-jwt") {
        post {
            val user = call.loggedUser()
            val eventoDto = call.receive<EventoInputDto>()
            eventoDto.validate()
            val evento = EventoRepo.save(user.id, eventoDto)
            UsuarioRepo.crearEvento(user.id, evento.id)
            call.respond(HttpStatusCode.Created, evento.toDto(user.toResponseDto()))
        }
    }
    route("/{id}") {
        eventosId()
    }
}