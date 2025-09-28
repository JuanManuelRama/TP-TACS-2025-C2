package com.g7.routing.eventos

import com.g7.evento.toDto
import com.g7.server.eventoRepo
import com.g7.server.requireIdParam
import com.g7.server.usuarioRepo
import com.g7.usuario.dto.toResponseDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.eventoRoutes() {
    get {
        val eventos = application.eventoRepo.getEventos()
        val maps = application.usuarioRepo.batchGetFromId(eventos.map { it.organizador }.toSet())
        val dtos = eventos.map { evento -> evento.toDto(maps[evento.organizador]!!.toResponseDto()) }
        call.respond(HttpStatusCode.OK, dtos)
    }

    get("/{id}") {
        val id = call.requireIdParam("id")
        val evento = application.eventoRepo.getFromId(id)
        call.respond(HttpStatusCode.OK, evento.toDto(application.usuarioRepo))
    }
    authenticate("auth-jwt") {
        eventoLoggedRoutes()
    }

}
