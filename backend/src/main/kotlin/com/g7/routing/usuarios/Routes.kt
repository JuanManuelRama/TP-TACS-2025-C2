package com.g7.routing.usuarios

import com.g7.application.middleware.login.JwtConfig
import com.g7.repo.UsuarioRepo
import com.g7.routing.usuarios.id.usuariosId
import com.g7.routing.usuarios.login.usuariosLogin
import com.g7.usuario.dto.LoginResponseDto
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
        val response = LoginResponseDto(
            token = JwtConfig.generateToken(newUsuario),
            user = newUsuario.toResponseDto()
        )
        call.respond(HttpStatusCode.Created, response)
    }

    route("/{id}") {
        usuariosId()
    }
    route("/login") {
        usuariosLogin()
    }

}
