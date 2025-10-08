package com.g7.routing.usuarios.id

import com.g7.repo.UsuarioRepo
import com.g7.application.requireIdParam
import com.g7.usuario.toResponseDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.usuariosId() {
    get {
        val id = call.requireIdParam("id")
        call.respond(HttpStatusCode.OK, UsuarioRepo.getFromId(id).toResponseDto())
    }
}