package com.g7.routing.usuarios

import com.g7.repo.UsuarioRepo
import com.g7.routing.usuarios.id.usuariosId
import com.g7.routing.usuarios.login.usuariosLogin
import com.g7.usuario.dto.UsuarioInputDto
import com.g7.usuario.dto.toResponseDto
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.usuarios() {
    post {
        val usuarioDto = call.receive<UsuarioInputDto>()

        val newUsuario = UsuarioRepo.save(usuarioDto)
        call.respond(HttpStatusCode.Created, newUsuario.toResponseDto())
    }

    route("/{id}") {
        usuariosId()
    }
    route("/login") {
        usuariosLogin()
    }

}
