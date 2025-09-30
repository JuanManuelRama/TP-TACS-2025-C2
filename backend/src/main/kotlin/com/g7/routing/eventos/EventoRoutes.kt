package com.g7.routing.eventos

import com.g7.evento.toDto
import com.g7.repo.EventoRepo
import com.g7.repo.UsuarioRepo
import com.g7.server.requireIdParam
import com.g7.usuario.dto.toResponseDto
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.eventoRoutes() {
    get {
        val eventos = EventoRepo.getEventos()
        val maps = UsuarioRepo.batchGetFromId(eventos.map { it.organizador }.toSet())
        val dtos = eventos.map { evento -> evento.toDto(maps[evento.organizador]!!.toResponseDto()) }
        call.respond(HttpStatusCode.OK, dtos)
    }

    get("/{id}") {
        val id = call.requireIdParam("id")
        val evento = EventoRepo.getFromId(id)
        call.respond(HttpStatusCode.OK, evento.toDto(UsuarioRepo))
    }
    authenticate("auth-jwt") {
        eventoLoggedRoutes()
    }

}
